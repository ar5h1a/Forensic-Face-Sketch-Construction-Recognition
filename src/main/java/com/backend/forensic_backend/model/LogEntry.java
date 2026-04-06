package com.backend.forensic_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "logs")
@Data
public class LogEntry {

    @Id
    @GeneratedValue
    private Long id;

    private String action;
    private String username;
    private String timestamp;
}
