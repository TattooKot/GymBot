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
import java.util.List;

@Component
public class ClientView {

    private final ClientController controller;

    public ClientView(ClientController controller) {
        this.controller = controller;
    }

    public SendMessage start(Update update){
        String result = "All commands:\n" + "\n" +
                "/get_all - all active" + "\n" +
                "/get_all_all - get all" + "\n" +
                "/pay_soon - pay soon" + "\n" +
                "/create - create new client" + "\n" +
                "/delete {id} - delete by id" + "\n" +
                "/send {text} - send all" + "\n" +
                "/all_connected - all connected to bot" + "\n" +
                "{id} - get user by id" + "\n" +
                "{date} {id..} - add visit" + "\n" +
                "Add pay {date} {id} - add payment" + "\n" +
                "Delete {id} - set active false" + "\n" +
                "Active {id} - set active true" + "\n" +
                "Phone {id} {phone} - update phone" + "\n";

        return createResponseMessage(update, result);
    }

    public SendMessage getAll(Update update){
        List<Client> clientList = controller.getAll();
        return createResponseMessage(update, createStringFromListOfClients(clientList));
    }

    public SendMessage paySoon(Update update){
        List<Client> clientList = controller.paySoon();
        return createResponseMessage(update, createStringFromListOfClients(clientList));
    }

    public SendMessage getById(Update update){
        String request = update.getMessage().getText();
        int id = checkId(request);
        if(id == -1){
            return createResponseMessage(update, "Id не знайдено, спробуй ще раз! \n");
        }

        return createResponseMessage(update, controller.getById(id).toString());
    }

    public SendMessage notActive(Update update){
        String request = update.getMessage().getText();

        if(request.length() <= 7){
            return createResponseMessage(update, "Щоб деактивувати користувача вкажіть його id");
        }

        int id = checkId(request.substring(7));
        if(id == -1){
            return createResponseMessage(update, "Неправильний id");
        }
        controller.notActive(id);
        return createResponseMessage(update, id + " деактивовано");
    }

    public SendMessage activeAgain(Update update){
        String request = update.getMessage().getText();

        if(request.length() <= 7){
            return createResponseMessage(update, "Щоб активувати користувача вкажіть його id");
        }

        int id = checkId(request.substring(7));
        if(id == -1){
            return createResponseMessage(update, "Неправильний id");
        }
        controller.activeAgain(id);
        return createResponseMessage(update, id + " активовано");
    }

    public SendMessage addVisit(Update update) {
        String request = update.getMessage().getText();
        String date = request.substring(0,5);

        if(request.length() == 5)
            return createResponseMessage(update, "Щоб додати візити вкажіть дату та індекси через ' '  \n");

        List<String> clients = List.of(request.substring(6).split(" "));

        for(String client : clients){
            int id = checkId(client);
            if(id == -1){
                return createResponseMessage(update, "Неправильний Id: " + id);
            }
        }
        return createResponseMessage(update,controller.addVisit(clients, date)
                + "\n" + createStringFromListOfClients(controller.getAll()));

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

    public SendMessage updatePhone(Update update){
        String request = update.getMessage().getText().replace("Phone ", "").trim();
        if(!request.contains(" ")){
            return createResponseMessage(update, "Bad command");
        }

        String[] data = request.split(" ");
        if(checkId(data[0]) == -1){
            return createResponseMessage(update, "Id does not exist");
        }
        if(!data[1].matches("^\\d{10}$")){
            return createResponseMessage(update, "Bad phone number");
        }
        Client client = controller.getById(Integer.parseInt(data[0]));
        client.setPhone(data[1]);
        return createResponseMessage(update, "Phone updated\n\n" + controller.update(client));
    }

    public SendMessage getAbsolutelyAll(Update update){
        return createResponseMessage(update, createStringFromListOfClients(controller.getAbsolutelyAll()));
    }

    public SendMessage deleteById(Update update){
        String data = update.getMessage().getText().substring(8);
        int id = checkId(data);
        if(id == -1){
            return createResponseMessage(update, "Неправильний id");
        }
        controller.deleteById(id);
        return createResponseMessage(update, "Користувач видалений: " + id);
    }

    public SendMessage allConnectedToBot(Update update){
        return createResponseMessage(update, createStringFromListOfClients(controller.allConnectedToBot()));
    }

    public SendMessage sendToAllUsers(Update update){
        String text = update.getMessage().getText().trim();
        if(text.equals("/send")){
            return createResponseMessage(update, "Після /send напиши повідомлення");
        }
        text = text.replace("/send", "").trim();

        controller.sendToAllUsers(text);
        return createResponseMessage(update, "Повідомлення надіслано");
    }

    public SendMessage bestFrau(Update update){
        return createResponseMessage(update, "You the best frau ever ;)");
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

    private String createStringFromListOfClients(List<Client> clientList){
        StringBuilder stringBuilder = new StringBuilder();

        for(Client client : clientList) {
            if (client.getCount() >= 8) {
                stringBuilder.append(String.format("%d.%s: %d(!)",
                        client.getId(), client.getName(), client.getCount()));

            } else if (client.getLastday().minusDays(7).isBefore(LocalDate.now())) {
                stringBuilder.append(String.format("%d.%s: %d(t)",
                        client.getId(), client.getName(), client.getCount()));
            } else {
                stringBuilder.append(String.format("%d.%s: %d",
                        client.getId(), client.getName(), client.getCount()));
            }

            if(!client.isActive()){
                stringBuilder.append("(N/A)");
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}