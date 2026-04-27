package com.vaultify.service;

// import java.net.http.*;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value; 
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupabaseStorageService {
    
    private final RestTemplate restTemplate;

    public SupabaseStorageService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;


    public String uploadFile(MultipartFile file) throws IOException{

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        String endPoint = supabaseUrl+"/storage/v1/object/"+bucket+"/"+fileName;

        HttpHeaders headers = new HttpHeaders();

        headers.set("apiKey", supabaseKey);
        headers.set("Authorization","Bearer " + supabaseKey);
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

}
