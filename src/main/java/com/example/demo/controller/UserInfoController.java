package com.example.demo.controller;

import com.example.demo.repository.ClientRepositoryImpl;
import org.springframework.stereotype.Component;

@Component
public class UserInfoController {

    private final ClientRepositoryImpl repository;

    public UserInfoController(ClientRepositoryImpl repository) {
        this.repository = repository;
    }
}
