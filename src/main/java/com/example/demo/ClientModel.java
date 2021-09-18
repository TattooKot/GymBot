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

    public SendMessage add(Update update) {
        String request = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        if(request.length() == 3)
            return new SendMessage(chatId,"Щоб додати користувача внесіть данні через ','  \n");

        return new SendMessage(chatId,"Щоб додати користувача внесіть данні через ','  \n");
    }

    public SendMessage addVisit(Update update) {
        String request = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        String date = request.substring(0,4);

        if(request.length() == 5)
            return new SendMessage(chatId,"Щоб додати візити вкажіть дату та індекси через ','  \n");



        String[] visitors =  request.substring(6).split(" ");

        for(String s : visitors) {
            if (!request.matches("[0-9]+") && s.length() >= 3)
                return new SendMessage(chatId, s + " не правильний id");

            int id = Integer.parseInt(s);
            if(!clientService.checkById(id))
                return new SendMessage(chatId, "Користувача з id " + id + " не знайдено");
        }

        StringBuilder result = new StringBuilder(date + "\n");

        for(String s : visitors){
            int id = Integer.parseInt(s);
            Client client = clientService.getById(id);

            if(!client.getFrequency().contains(date)) {
                client.setFrequency(date + "," + client.getFrequency());
                client.setCount(client.getCount() + 1);
                result.append(clientService.update(client).getName() + "\n");
            }else result.append(clientService.update(client).getName() + "(++)" + "\n");

        }

        SendMessage all = getAll(update);




        return new SendMessage(chatId,result.toString() + "\n" + all.getText());

    }
}
