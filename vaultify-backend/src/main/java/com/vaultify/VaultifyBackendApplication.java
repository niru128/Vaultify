package com.vaultify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EntityScan("com.vaultify.model")
public class VaultifyBackendApplication {

	 @Value("${DB_URL:NOT_FOUND}")
        private String dbUrl;

        @PostConstruct
        public void printDbUrl
            
        () {
    System.out.println("DB_URL = " + dbUrl);
        }

    public static void main(String[] args) {

       
        SpringApplication.run(VaultifyBackendApplication.class, args);
    }

}
