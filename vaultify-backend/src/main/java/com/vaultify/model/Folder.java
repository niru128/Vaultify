package com.vaultify.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;



@Entity
public class Folder{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String ownerEmail;
    private LocalDateTime createdAt;

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getOwnerEmail(){
        return ownerEmail;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setOwnerEmail(String ownerEmail){
        this.ownerEmail = ownerEmail;
    }

    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }
}