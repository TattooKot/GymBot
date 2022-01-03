package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepositoryImpl;
import org.springframework.stereotype.Component;

@Component
public class UserBotController extends CrudController{
    public UserBotController(CustomerRepositoryImpl repository) {
        super(repository);
    }

    public Customer getByPhone(String phone){
        return getAbsolutelyAll().stream()
                .filter(c -> c.getPhone().equals(phone))
                .findFirst()
                .orElse(null);
    }

    public Customer getByChatId(Integer chatId){
        return getAbsolutelyAll().stream()
                .filter(c -> c.getChatId().equals(chatId))
                .findFirst()
                .orElse(null);
    }
}
