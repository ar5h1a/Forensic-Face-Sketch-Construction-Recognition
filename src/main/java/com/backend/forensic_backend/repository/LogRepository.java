package com.backend.forensic_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.forensic_backend.model.LogEntry;

public interface LogRepository extends JpaRepository<LogEntry, Long> {}