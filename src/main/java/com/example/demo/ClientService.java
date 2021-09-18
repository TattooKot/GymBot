package com.example.demo;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAll(){
        return clientRepository.findAll();
    }

    public boolean checkById(int id){
        return clientRepository.existsById(id);
    }

    public Client getById(int id){
        return clientRepository.findById(id).orElse(null);
    }

}
