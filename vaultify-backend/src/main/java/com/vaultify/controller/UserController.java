package com.vaultify.controller;

// import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        return Map.of(
            "name", authentication.getName()
        );
    }
}
