package com.vaultify.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vaultify.model.FileRecord;
import com.vaultify.repository.FileRecordRepository;
import com.vaultify.service.SupabaseStorageService;


@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileRecordRepository fileRecordRepository;
    private final SupabaseStorageService supabaseStorageService;


    public FileController(FileRecordRepository fileRecordRepository, SupabaseStorageService supabaseStorageService) {
        this.fileRecordRepository = fileRecordRepository;
        this.supabaseStorageService = supabaseStorageService;
    }

    @PostMapping("/upload")
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws IOException {

        String email = authentication.getName();

        String fileName = supabaseStorageService.uploadFile(file);

        FileRecord record = new FileRecord();
        record.setFileName(fileName);
        record.setFilePath(fileName);
        record.setSize(file.getSize());
        record.setOwnerEmail(email);
        record.setUploadTime(LocalDateTime.now());

        fileRecordRepository.save(record);

        return "File uploaded successfully to Supabase with name: " + fileName;
    }

    @GetMapping("/my-files")
    public List<FileRecord> getMyFiles(Authentication authentication) {
        String email = authentication.getName();
        return fileRecordRepository.findByOwnerEmail(email);
    }

    @DeleteMapping("/{id}")
    public String deleteFile(@PathVariable Long id, Authentication authentication) {

        String email = authentication.getName();

        FileRecord record = fileRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));

        if (!record.getOwnerEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        File file = new File(record.getFilePath());

        if (file.exists()) {
            file.delete();
        }

        fileRecordRepository.delete(record);

        return "File deleted Successfully";
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



}
