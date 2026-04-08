package com.funding.funding.domain.upload.controller;

import com.funding.funding.domain.project.service.create.ImageStorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
public class ImageUploadController {

    private final ImageStorageService imageStorageService;

    public ImageUploadController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("/images")
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = imageStorageService.save(file);
        return Map.of("url", url);
    }
}