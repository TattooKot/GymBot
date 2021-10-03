package com.example.demo.repository;

import com.example.demo.model.Client;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientRepositoryImpl {

    private final ClientRepository clientRepository;

    public ClientRepositoryImpl(ClientRepository clientRepository) {
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

    public Client update(Client client){
        return clientRepository.save(client);
    }

}
