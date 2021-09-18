package com.example.demo;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;

@Component
public class ClientModel {

    private final ClientService clientService;

    public ClientModel(ClientService clientService) {
        this.clientService = clientService;
    }

    public SendMessage getAll(Update update){
        List<Client> clients = clientService.getAll();

        class ClientComparator implements Comparator<Client> {

            public int compare(Client a, Client b){

                return a.getId().compareTo(b.getId());
            }
        }

        clients.sort(new ClientComparator());

        StringBuilder stringBuilder = new StringBuilder();
        clients.forEach(client -> stringBuilder.append(String.format("%d.%s: %d", client.getId(),client.getName(), client.getCount())).append("\n"));

        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(stringBuilder.toString());

        return message;
    }

    public SendMessage getById(Update update){
        String request = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        if(request.length() > 3 && request.length() <= 5)
           request = request.substring(3);

        if(request.length() > 6)
            return new SendMessage(chatId,"Щоб отримати інформацію, виконай команду id {id}");

        if(!request.matches("[0-9]+"))
            return new SendMessage(chatId,"Id це тільки цифри, спробуй ще раз! \n");

        int id = Integer.parseInt(request);
        if(!clientService.checkById(id))
            return new SendMessage(chatId,"Id не знайдено, спробуй ще раз! \n");

        return new SendMessage(chatId,clientService.getById(id).toString());
    }

    public SendMessage bestFrau(Update update){
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText("You the best frau ever ;)");

        return message;
    }
}
