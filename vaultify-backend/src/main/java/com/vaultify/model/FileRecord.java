package com.vaultify.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file")
public class FileRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String filePath;
    private Long folderId;
    private String ownerEmail;
    private Long size;
    private LocalDateTime uploadTime;
    
    public Long getFolderId(){
        return folderId;
    }

    public void setFolderId(Long folderId){
        this.folderId = folderId;
    }
    
}
