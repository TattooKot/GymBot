package com.example.demo.controller;

import com.example.demo.model.Client;
import com.example.demo.repository.ClientRepositoryImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientController {
    private final ClientRepositoryImpl repository;

    public ClientController(ClientRepositoryImpl repository) {
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

    public List<Client> paySoon() {
        return repository.paySoon();
    }

    public boolean checkById(int id){
        return repository.checkById(id);
    }

    public Client getById(int id){
        return repository.getById(id);
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

    public Client createNew(Client client){
        return repository.create(client);
    }

    public Client update(Client client) {
        return repository.update(client);
    }

    public String addVisit(List<String> stringIdList, String date){
        List<Client> numIdList = stringIdList.stream()
                .map(Integer::parseInt)
                .map(this::getById)
                .collect(Collectors.toList());

        StringBuilder result = new StringBuilder(date + "\n");

        for(Client currentClient : numIdList) {

            if (!currentClient.getFrequency().contains(date)) {
                if (currentClient.getCount() == 10) {
                    currentClient.setFrequency(date + "(!)," + currentClient.getFrequency());
                    currentClient.setCount(1);
                    currentClient.setName(currentClient.getName() + "(!)");
                    currentClient.setPayday(LocalDate.now());
                } else {
                    currentClient.setFrequency(date + "," + currentClient.getFrequency());
                    currentClient.setCount(currentClient.getCount() + 1);
                }

                result.append(update(currentClient).getName()).append("\n");
            } else result.append(update(currentClient).getName()).append("(++)").append("\n");
        }
        return result.toString();
    }
    
    public Client addPayment(LocalDate payDay, int id){
        Client client = getById(id);
        client.setPayday(payDay);
        client.setCount(1);
        client.setFrequency(payDay.format(DateTimeFormatter.ofPattern("dd.MM"))+ "(payday)," + client.getFrequency());
        return update(client);
    }

    public void notActive(int id){
        Client client = getById(id);
        client.setActive(false);
        update(client);
    }

    public void activeAgain(int id){
        Client client = getById(id);
        client.setActive(true);
        update(client);
    }

    public void deleteById(int id){
        repository.deleteById(id);
    }

}