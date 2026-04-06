package com.backend.forensic_backend.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.forensic_backend.model.Sketch;
import com.backend.forensic_backend.service.SketchService;

@RestController
@RequestMapping("/api/sketch")
@CrossOrigin("*")
public class SketchController {

    @Autowired
    private SketchService service;

@PostMapping("/upload")
public Sketch upload(@RequestParam("file") MultipartFile file) throws Exception {

    String uploadDirPath = "C:/uploads/";
    File uploadDir = new File(uploadDirPath);

    // create folder if it doesn't exist
    if (!uploadDir.exists()) {
        uploadDir.mkdirs();
    }

    String filePath = uploadDirPath + file.getOriginalFilename();
    File destination = new File(filePath);

    file.transferTo(destination);

    Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
    String username= authentication.getName();
    return service.saveSketch(filePath, username);
}
}