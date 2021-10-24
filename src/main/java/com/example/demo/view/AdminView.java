package com.example.demo.view;


import com.example.demo.controller.AdminBotController;
import com.example.demo.model.Client;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AdminView {

    private final AdminBotController controller;

    public AdminView(AdminBotController controller) {
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
                "Phone {id} {phone} - update phone" + "\n" +
                "Count {id} {count} - update count" + "\n";

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
        String request = update.getMessage().getText().replace("Delete", "").trim();

        int id = checkId(request);
        if(id == -1){
            return createResponseMessage(update, "Неправильний id");
        }
        controller.notActive(id);
        return createResponseMessage(update, id + " деактивовано");
    }

    public SendMessage activeAgain(Update update){
        String request = update.getMessage().getText().replace("Active", "").trim();

        int id = checkId(request);
        if(id == -1){
            return createResponseMessage(update, "Неправильний id");
        }
        controller.activeAgain(id);
        return createResponseMessage(update, id + " активовано");
    }

    public SendMessage addVisit(Update update) {
        String request = update.getMessage().getText();
        String date = request.substring(0,request.indexOf(" "));

        List<String> clients = new ArrayList<>(
                List.of(request.substring(request.indexOf(" "))
                        .trim()
                        .split(" ")));

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
        String[] data = getDataArrayFromRequest(update, "Add pay");

        if(Objects.isNull(data)){
            return createResponseMessage(update, "Bad command");
        }

        int id = checkId(data[0]);
        String date = data[1] + ".2021";

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

    }

    public SendMessage updatePhone(Update update){
        String[] data = getDataArrayFromRequest(update, "Phone");

        if(Objects.isNull(data)){
            return createResponseMessage(update, "Bad command");
        }

        String id = data[0];
        String phone = data[1];

        if(checkId(id) == -1){
            return createResponseMessage(update, "Id does not exist");
        }
        if(!phone.matches("^\\d{10}$")){
            return createResponseMessage(update, "Bad phone number");
        }

        return createResponseMessage(update, "Phone updated\n\n"
                + controller.updatePhoneById(Integer.parseInt(id), phone));
    }

    public SendMessage updateCount(Update update){
        String[] data = getDataArrayFromRequest(update, "Count");

        if(Objects.isNull(data)){
            return createResponseMessage(update, "Bad command");
        }

        int id = checkId(data[0]);
        String count = data[1];

        if(id == -1){
            return createResponseMessage(update, "Id does not exist");
        }

        if(!count.matches("^\\d{1,2}$")
                || Integer.parseInt(data[1]) > 10
                || Integer.parseInt(data[1]) < 0)
        {
            return createResponseMessage(update, "Bad count");
        }

        return createResponseMessage(update, "Count updated:\n"
                + controller.updateCountById(id, Integer.parseInt(count)));
    }

    public SendMessage updateName(Update update){
        String[] data = getDataArrayFromRequest(update,"Name");

        if(Objects.isNull(data)){
            return createResponseMessage(update, "Bad command");
        }

        int id = checkId(data[0]);
        String name = data[1];

        if(id == -1){
            return createResponseMessage(update, "Id does not exist");
        }

        return createResponseMessage(update, "Name updated:\n" + controller.updateNameById(id, name));
    }

    public SendMessage getAbsolutelyAll(Update update){
        return createResponseMessage(update, createStringFromListOfClients(controller.getAbsolutelyAll()));
    }

    public SendMessage deleteById(Update update){
        String data = update.getMessage().getText().replace("/delete", "").trim();
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

    private SendMessage createResponseMessage(Update update, String text){
        String chatId = update.getMessage().getChatId().toString();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if(!chatId.equals("329606734")){
            message.setText("Wrong chat id");
        }else {
            message.setText(text);
        }
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

        stringBuilder.append("\n").append("Count: ").append(clientList.size());

        return stringBuilder.toString();
    }

    private String[] getDataArrayFromRequest(Update update, String command){
        String request = update.getMessage().getText().replace(command, "").trim();

        if(!request.contains(" ")){
            return null;
        }

        return request.split(" ");
    }
}