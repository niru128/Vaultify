package com.vaultify.service;

// import java.net.http.*;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupabaseStorageService {

    private final RestTemplate restTemplate;

    public SupabaseStorageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) throws IOException {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        String endPoint = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();

        headers.set("apiKey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

        restTemplate.exchange(
                endPoint,
                HttpMethod.POST,
                entity,
                String.class
        );

        return fileName;

    }

    @SuppressWarnings("null")
    public String generateSignedUrl(String fileName) {

        String endpoint = supabaseUrl
                + "/storage/v1/object/sign/"
                + bucket + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{ \"expiresIn\": 60 }";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                entity,
                Map.class
        );

        String signedPath = response.getBody().get("signedURL").toString();

        return supabaseUrl + "/storage/v1" + signedPath;
    }

    public void deleteFile(String fileName) {

    String endpoint = supabaseUrl +
            "/storage/v1/object/" +
            bucket + "/" + fileName;

    HttpHeaders headers = new HttpHeaders();
    headers.set("apikey", supabaseKey);
    headers.set("Authorization", "Bearer " + supabaseKey);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    try {
        restTemplate.exchange(
                endpoint,
                HttpMethod.DELETE,
                entity,
                String.class
        );
    } catch (RestClientException e) {
        System.out.println("Supabase delete failed: " + e.getMessage());
    }
}
}
