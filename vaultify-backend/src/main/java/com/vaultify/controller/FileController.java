package com.vaultify.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vaultify.model.FileRecord;
import com.vaultify.model.User;
import com.vaultify.repository.FileRecordRepository;
import com.vaultify.repository.UserRepository;
import com.vaultify.service.SupabaseStorageService;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileRecordRepository fileRecordRepository;
    private final SupabaseStorageService supabaseStorageService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    // private final FolderRepository folderRepository;

    public FileController(FileRecordRepository fileRecordRepository, SupabaseStorageService supabaseStorageService, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.fileRecordRepository = fileRecordRepository;
        this.supabaseStorageService = supabaseStorageService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        // this.folderRepository = folderRepository;
    }

    @PostMapping("/upload")
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long folderId,
            Authentication authentication
    ) throws IOException {

        String email = authentication.getName();

        String fileName = supabaseStorageService.uploadFile(file);

        FileRecord record = new FileRecord();
        record.setFileName(file.getOriginalFilename());
        record.setFilePath(fileName);
        record.setSize(file.getSize());
        record.setOwnerEmail(email);
        record.setFolderId(folderId);
        record.setUploadTime(LocalDateTime.now());

        fileRecordRepository.save(record);

        return "File uploaded successfully to Supabase with name: " + fileName;
    }

    @GetMapping("/my-files")
    public List<FileRecord> getMyFiles(@RequestParam(required = false) Long folderId, Authentication authentication) {
        String email = authentication.getName();

        if (folderId == null) {

            return fileRecordRepository.findByOwnerEmailAndFolderIdIsNull(email);
        }

        return fileRecordRepository.findByOwnerEmailAndFolderId(email, folderId);
    }

    @DeleteMapping("/{id}")
    public String deleteFile(
            @PathVariable Long id,
            @RequestParam(required = false) String password,
            Authentication authentication
    ) {

        FileRecord record = fileRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!record.getOwnerEmail().equals(authentication.getName())) {
            throw new RuntimeException("Unauthorized");
        }

        if (Boolean.TRUE.equals(record.getIsProtected())) {
            if (password == null
                    || !passwordEncoder.matches(password, record.getFilePasswordHash())) {
                throw new RuntimeException("Wrong file password");
            }
        }

        supabaseStorageService.deleteFile(record.getFilePath());
        fileRecordRepository.delete(record);

        return "Deleted successfully";
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id,
            Authentication authentication
    ) throws IOException {

        String email = authentication.getName();

        FileRecord record = fileRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!record.getOwnerEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        Path path = Paths.get(record.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + record.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/download-link")
    public String getSignedLink(
            @PathVariable Long id,
            @RequestParam(required = false) String password,
            Authentication authentication
    ) {

        FileRecord record = fileRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!record.getOwnerEmail().equals(authentication.getName())) {
            throw new RuntimeException("Unauthorized");
        }

        if (Boolean.TRUE.equals(record.getIsProtected())) {
            if (password == null
                    || !passwordEncoder.matches(password, record.getFilePasswordHash())) {
                throw new RuntimeException("Wrong file password");
            }
        }

        return supabaseStorageService.generateSignedUrl(record.getFilePath());
    }

    @PostMapping("/{id}/enable-protection")
    public String enableProtection(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {

        FileRecord file = fileRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // 🔒 Check owner
        if (!file.getOwnerEmail().equals(authentication.getName())) {
            throw new RuntimeException("Unauthorized");
        }

        String accountPassword = body.get("accountPassword");
        String filePassword = body.get("filePassword");

        if (accountPassword == null || filePassword == null || filePassword.isEmpty()) {
            throw new RuntimeException("Passwords required");
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(accountPassword, user.getPassword())) {
            throw new RuntimeException("Invalid account password");
        }

        file.setIsProtected(true);
        file.setFilePasswordHash(passwordEncoder.encode(filePassword));

        fileRecordRepository.save(file);

        return "Protection enabled";
    }

}
