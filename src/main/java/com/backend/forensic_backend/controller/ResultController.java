package com.backend.forensic_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.forensic_backend.model.Result;
import com.backend.forensic_backend.repository.ResultRepository;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @Autowired
    private ResultRepository repo;

    @GetMapping
    public List<Result> getAll() {
        return repo.findAll();
    }
}