package com.backend.forensic_backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.forensic_backend.model.Sketch;
import com.backend.forensic_backend.repository.SketchRepository;

@Service
public class SketchService {

    @Autowired
    private SketchRepository repo;

    public Sketch saveSketch(String path, String user) {
        Sketch s = new Sketch();
        s.setImageUrl(path);
        s.setUploadedBy(user);
        s.setCreatedAt(LocalDateTime.now().toString());

        return repo.save(s);
    }
}