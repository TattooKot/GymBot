package com.example.demo.repository;

import com.example.demo.model.Client;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class ClientRepositoryImpl {

    private final ClientRepository clientRepository;

    public ClientRepositoryImpl(@Lazy ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAll(){
        return getAllInternal();
    }

    public List<Client> paySoon(){
        List<Client> clientList = new ArrayList<>();
        getAllInternal().stream()
                .filter(Client::isActive)
                .forEach(c ->{
            if(c.getCount() >= 8 || (c.getLastday().minusDays(7).isBefore(LocalDate.now()))){
                clientList.add(c);
            }
        });
        return clientList;
    }

    public Client create (Client client){
        return clientRepository.save(client);
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

    private List<Client> getAllInternal(){
        List<Client> clientList = clientRepository.findAll();
        clientList.sort(Comparator.comparingInt(Client::getId));
        return clientList;
    }
}
