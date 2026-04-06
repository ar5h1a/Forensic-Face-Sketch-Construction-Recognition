package com.backend.forensic_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.forensic_backend.model.Sketch;

public interface SketchRepository extends JpaRepository<Sketch, Long> {}
