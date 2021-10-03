package com.example.demo.controller;

import com.example.demo.model.Client;
import com.example.demo.repository.ClientRepositoryImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ClientController {
    private final ClientRepositoryImpl repository;

    public ClientController(ClientRepositoryImpl repository) {
        this.repository = repository;
    }

    public List<Client> getAll(){
        return repository.getAll();
    }

    public List<Client> paySoon() {
        return repository.paySoon();
    }

    public boolean checkById(int id){
        return repository.checkById(id);
    }

    public Client getById(int id){
        return repository.getById(id);
    }

    public Client update(Client client) {
        return repository.update(client);
    }
    
    public Client addPayment(LocalDate payDay, int id){
        Client client = getById(id);
        client.setPayday(payDay);
        client.setCount(0);
        client.setFrequency(payDay.format(DateTimeFormatter.ofPattern("dd.MM"))+ "(payday)," + client.getFrequency());
        return update(client);
    }


}