package com.example.demo.controller;

import com.example.demo.model.Client;
import com.example.demo.repository.ClientRepositoryImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CrudController {
    protected final ClientRepositoryImpl repository;

    public CrudController(ClientRepositoryImpl repository) {
        this.repository = repository;
    }

    public List<Client> getAll(){
        List<Client> clientList = repository.getAll();
        clientList.removeIf(c -> !c.isActive());
        return clientList;
    }

    public List<Client> getAbsolutelyAll(){
        return repository.getAll();
    }

    public boolean checkById(int id){
        return repository.checkById(id);
    }

    public Client getById(int id){
        return repository.getById(id);
    }

    public Client createNew(Client client){
        return repository.create(client);
    }

    public Client update(Client client) {
        return repository.update(client);
    }
}
