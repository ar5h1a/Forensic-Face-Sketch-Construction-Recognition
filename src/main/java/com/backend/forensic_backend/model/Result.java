package com.backend.forensic_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "results")
@Data
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sketch_id")
    private Sketch sketch;

    private String matchedPerson;
    private String matchedFile;
    private double confidence;
}

