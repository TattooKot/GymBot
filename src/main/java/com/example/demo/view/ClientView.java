package com.example.demo.view;


import com.example.demo.controller.ClientController;
import com.example.demo.model.Client;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Component
public class ClientView {

    private final ClientController controller;

    public ClientView(ClientController controller) {
        this.controller = controller;
    }

    public SendMessage getAll(Update update){
        List<Client> clientList = controller.getAll();
        return createResponseMessage(update, createStringForListOfClients(clientList));
    }

    public SendMessage paySoon(Update update){
        List<Client> clientList = controller.paySoon();
        return createResponseMessage(update, createStringForListOfClients(clientList));
    }

    public SendMessage getById(Update update){
        String request = update.getMessage().getText();
        int id = checkId(request);
        if(id == -1){
            return createResponseMessage(update, "Id не знайдено, спробуй ще раз! \n");
        }

        return createResponseMessage(update, controller.getById(id).toString());
    }

    public SendMessage bestFrau(Update update){
        return createResponseMessage(update, "You the best frau ever ;)");
    }

    public SendMessage addVisit(Update update) {
        String request = update.getMessage().getText();
        String date = request.substring(0,5);

        if(request.length() == 5)
            return createResponseMessage(update, "Щоб додати візити вкажіть дату та індекси через ','  \n");

        List<String> clients = List.of(request.substring(6).split(" "));
        StringBuilder result = new StringBuilder(date + "\n");

        for(String client : clients){
            int id = checkId(client);
            if(id == -1){
                return createResponseMessage(update, "Неправильний Id");
            }

            Client currentClient = controller.getById(id);

            if(!currentClient.getFrequency().contains(date)) {
                if(currentClient.getCount() == 10) {
                    currentClient.setFrequency(date + "(!)," + currentClient.getFrequency());
                    currentClient.setCount(1);
                    currentClient.setName(currentClient.getName() + "(!)");
                    currentClient.setPayday(LocalDate.now());
                }
                else {
                    currentClient.setFrequency(date + "," + currentClient.getFrequency());
                    currentClient.setCount(currentClient.getCount() + 1);
                }

                result.append(controller.update(currentClient).getName()).append("\n");
            }else result.append(controller.update(currentClient).getName()).append("(++)").append("\n");
        }

        return createResponseMessage(update,result + "\n" + controller.getAll().toString());

    }

    public SendMessage addPayment(Update update) {
        String request = update.getMessage().getText();

        if(request.length() == 7) {
            return createResponseMessage(update, "Щоб додати оплату вкажіть дату та id\n");
        } else if(request.length() == 13){
            return createResponseMessage(update, "Щоб додати оплату вкажіть також id\n");
        }else if(request.length() == 15 || request.length() == 16){
            String date = request.substring(8, 13) + ".2021";
            int id = checkId(request.substring(14));

            if(id == -1){
                return createResponseMessage(update, "Id does not exist");
            }

            LocalDate payDay = null;

            try {
                payDay = new SimpleDateFormat("dd.MM.yyyy").parse(date)
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return createResponseMessage(update, "Payment added:\n" + controller.addPayment(payDay, id));

        } else {
            return createResponseMessage(update, "Помилка");
        }
    }

    private SendMessage createResponseMessage(Update update, String text){
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(text);

        return message;
    }

    private int checkId(String stringId){
        if(!stringId.matches("[0-9]+")) {
            return -1;
        }
        int id = Integer.parseInt(stringId);
        if(!controller.checkById(id)) {
            return -1;
        }
        return id;
    }

    private String createStringForListOfClients(List<Client> clientList){
        StringBuilder stringBuilder = new StringBuilder();
        int id = 1;

        for(Client client : clientList) {
            if (client.getCount() >= 8) {
                stringBuilder.append(String.format("%d.%s: %d(!)\n",
                        id++, client.getName(), client.getCount()));

            } else if (client.getLastday().minusDays(7).isBefore(LocalDate.now())) {
                stringBuilder.append(String.format("%d.%s: %d(t)\n",
                        id++, client.getName(), client.getCount()));
            } else {
                stringBuilder.append(String.format("%d.%s: %d\n",
                        id++, client.getName(), client.getCount()));
            }
        }

        return stringBuilder.toString();
    }
}
