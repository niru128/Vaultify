package com.vaultify.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaultify.model.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findByOwnerEmail(String ownerEmail);

}
