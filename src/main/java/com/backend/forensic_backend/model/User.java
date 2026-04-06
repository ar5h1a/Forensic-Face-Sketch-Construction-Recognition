package com.backend.forensic_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data 
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    
}
