package com.backend.forensic_backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.forensic_backend.model.LogEntry;
import com.backend.forensic_backend.repository.LogRepository;

@Service
public class LogService {

    @Autowired
    private LogRepository repo;

    public void log(String action, String user) {
        LogEntry log = new LogEntry();
        log.setAction(action);
        log.setUsername(user);
        log.setTimestamp(LocalDateTime.now().toString());

        repo.save(log);
    }
}