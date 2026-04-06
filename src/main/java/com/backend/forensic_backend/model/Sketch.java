package com.backend.forensic_backend.model;

import jakarta.persistence.*;
import lombok.Data;

    @Entity
@Table(name = "sketches")
@Data
public class Sketch {

    @Id
    @GeneratedValue
    private Long id;

    private String imageUrl; // S3 or local path
    private String uploadedBy;
    private String createdAt;
}
