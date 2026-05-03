package com.vaultify.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaultify.model.FileRecord;
import com.vaultify.model.Folder;
import com.vaultify.repository.FileRecordRepository;
import com.vaultify.repository.FolderRepository;
import com.vaultify.service.SupabaseStorageService;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderRepository folderRepository;
    private final FileRecordRepository fileRepo;
    private final SupabaseStorageService storageService;

    public FolderController(FolderRepository folderRepository, FileRecordRepository fileRepo, SupabaseStorageService storageService) {
        this.folderRepository = folderRepository;
        this.fileRepo = fileRepo;
        this.storageService = storageService;
    }

    @PostMapping
    public Folder createFolder(@RequestBody Map<String, String> body, Authentication authentication) {

        if (!body.containsKey("name") || body.get("name").isEmpty()) {
            throw new IllegalArgumentException("Folder name is required");
        }

        Folder folder = new Folder();
        folder.setName(body.get("name"));
        folder.setOwnerEmail(authentication.getName());
        folder.setCreatedAt(LocalDateTime.now());

        return folderRepository.save(folder);

    }

    @GetMapping
    public List<Folder> getFolders(Authentication authentication) {
        return folderRepository.findByOwnerEmail(authentication.getName());
    }

    @DeleteMapping("/{id}")
    public String deleteFolder(@PathVariable Long id, Authentication authentication) {

        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        if (!folder.getOwnerEmail().equals(authentication.getName())) {
            throw new RuntimeException("Unauthorized");
        }

        List<FileRecord> files = fileRepo.findByFolderId(id);

        for (FileRecord file : files) {
            storageService.deleteFile(file.getFilePath());
            fileRepo.delete(file);
        }

        folderRepository.delete(folder);

        return "Folder deleted successfully";
    }

}
