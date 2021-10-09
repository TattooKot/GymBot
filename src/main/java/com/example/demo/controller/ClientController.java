package com.example.demo.controller;

import com.example.demo.bots.UserInfoBot;
import com.example.demo.model.Client;
import com.example.demo.repository.ClientRepositoryImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientController {
    private final ClientRepositoryImpl repository;
    private final UserInfoBot userInfoBot;

    public ClientController(@Lazy ClientRepositoryImpl repository, @Lazy UserInfoBot userInfoBot) {
        this.repository = repository;
        this.userInfoBot = userInfoBot;
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
        List<Client> clientList = stringIdList.stream()
                .map(Integer::parseInt)
                .map(this::getById)
                .collect(Collectors.toList());

        StringBuilder result = new StringBuilder(date + "\n");

        for(Client currentClient : clientList) {

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

                sendToUsersInfoBot(currentClient, "Тренування " + date + " закінчено! \uD83D\uDE0E\n" +
                        "Машина, йомайо! \uD83D\uDE04");
                result.append(update(currentClient).getName()).append("\n");
            } else result.append(update(currentClient).getName()).append("(++)").append("\n");
        }
        return result.toString();
    }
    
    public Client addPayment(LocalDate payDay, int id){
        Client client = getById(id);

        //if trainings started already
        if(client.getName().contains("(!)")){
            client.setName(client.getName().replace("(!)", ""));
            sendToUsersInfoBot(client, "❗Додано 10 тренувань❗\n" +
                    "Нагадую що тренування дійсні\n" +
                    "Від: " +client.getPayday().format(DateTimeFormatter.ofPattern("dd.MM")) + "\n" +
                    "До: " + client.getLastday().format(DateTimeFormatter.ofPattern("dd.MM")));
            return update(client);
        }

        //if everything okay, add new 10 trainings
        client.setPayday(payDay);
        client.setCount(1);
        client.setFrequency(payDay.format(DateTimeFormatter.ofPattern("dd.MM"))+ "(payday)," + client.getFrequency());
        sendToUsersInfoBot(client, "❗Додано 10 тренувань❗\nНагадую, що тренування дійсні\n" +
                "Від: " +payDay.format(DateTimeFormatter.ofPattern("dd.MM")) + "\n" +
                "До: " + client.getLastday().format(DateTimeFormatter.ofPattern("dd.MM")));

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

    public void sendToUsersInfoBot(Client client, String text){
        if(client.getChatid() == 0){
            return;
        }
        userInfoBot.messageToUser(client.getChatid(), text);
    }

    public void sendToAllUsers(String text){
        getAll().stream()
                .filter(c -> !c.getChatid().equals(0))
                .forEach(c -> sendToUsersInfoBot(c,text));
    }

}