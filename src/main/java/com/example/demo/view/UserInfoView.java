package com.example.demo.view;

import com.example.demo.controller.ClientController;
import com.example.demo.model.Client;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

@Component
public class UserInfoView {

    private final ClientController clientController;

    public UserInfoView(ClientController clientController) {
        this.clientController = clientController;
    }

    public SendMessage start(Update update) {
        String text =
                "Хелоу! Їв? Спав?\n" +
                        "Щоб розпочати роботу з ботом, введи свій номер телефону в форматі '0500000000'";
        return createResponseMessage(update, text);
    }

    public SendMessage connect(Update update){
        String phone = update.getMessage().getText();
        int id = Integer.parseInt(update.getMessage().getChatId().toString());

        if(!checkPhone(phone)){
            return createResponseMessage(update, "Номер телефону не закріплений ні за ким в залі:(\n " +
                    "Спробуй ще раз");
        }

        Client client = clientController.getByPhone(phone);

        if(chatIdPresent(client)){
            return createResponseMessage(update, "Користувач з цим номером телефону вже зараєструвався");
        }
        client.setChatid(id);
        clientController.update(client);

        return createResponseMessage(update, "Окей, тепер все добре, нічого не болить, не тягне," +
                " і можна спробувати отримати інформацію по тренуванням.\n" +
                "-> /sho_tam");
    }

    public SendMessage info(Update update){
        int chatId = Integer.parseInt(update.getMessage().getChatId().toString());
        if(checkChatId(chatId)){
            return createResponseMessage(update, "Не поспішай!\n" +
                    "Щоб розпочати роботу з ботом, введи свій номер телефону в форматі '0500000000'");
        }
        return createResponseMessage(update, clientController.getByChatId(chatId).toString());
    }

    public SendMessage reset(Update update){
        int chatId = Integer.parseInt(update.getMessage().getChatId().toString());
        if(!checkChatId(chatId)){
            return createResponseMessage(update, "ChatId does not exist");
        }
        Client client = clientController.getByChatId(chatId);
        client.setChatid(0);
        clientController.update(client);
        return createResponseMessage(update, "ChatId reset completed");
    }

    private SendMessage createResponseMessage(Update update, String text){
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(text);

        return message;
    }

    private boolean checkPhone(String phone){
        Client client = clientController.getAbsolutelyAll().stream()
                .filter(n -> n.getPhone().equals(phone))
                .findFirst()
                .orElse(null);
        return Objects.nonNull(client);
    }

    private boolean checkChatId(int id){
        Client client = clientController.getAbsolutelyAll().stream()
                .filter(n -> n.getChatid() == id)
                .findFirst()
                .orElse(null);
        return Objects.isNull(client);
    }

    private boolean chatIdPresent(Client client){
        return client.getChatid() != 0;
    }
}
