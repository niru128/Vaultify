package com.vaultify.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaultify.model.FileRecord;

public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
    List<FileRecord> findByOwnerEmail(String ownerEmail);
    List<FileRecord> findByOwnerEmailAndFolderId(String ownerEmail, Long folderId);
    List<FileRecord> findByOwnerEmailAndFolderIdIsNull(String email);
    List<FileRecord> findByFolderId(Long folderId);
}
