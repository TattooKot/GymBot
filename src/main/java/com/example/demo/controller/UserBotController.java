package com.example.demo.controller;

import com.example.demo.model.Client;
import com.example.demo.repository.ClientRepositoryImpl;
import org.springframework.stereotype.Component;

@Component
public class UserBotController extends CrudController{
    public UserBotController(ClientRepositoryImpl repository) {
        super(repository);
    }

    public Client getByPhone(String phone){
        return getAbsolutelyAll().stream()
                .filter(c -> c.getPhone().equals(phone))
                .findFirst()
                .orElse(null);
    }

    public Client getByChatId(Integer chatId){
        return getAbsolutelyAll().stream()
                .filter(c -> c.getChatid().equals(chatId))
                .findFirst()
                .orElse(null);
    }
}
