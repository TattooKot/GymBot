package com.example.demo.view;

import com.example.demo.controller.ClientController;
import com.example.demo.model.Client;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

@Component
public class UserInfoView {

    private final ClientController controller;

    public UserInfoView(ClientController clientController) {
        this.controller = clientController;
    }

    public SendMessage help(Update update) {
        String text = "/reset - reset chatId\n" +
                "/sho_tam - get info by chat id";
        return createResponseMessage(update, text);
    }

    public SendMessage start(Update update) {
        int chatId = Integer.parseInt(update.getMessage().getChatId().toString());
        if(!checkChatId(chatId)){
            return createResponseMessage(update, "Для чого знову старт? Все ж уже працює");
        }

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

        Client client = controller.getByPhone(phone);

        if(chatIdPresent(client)){
            return createResponseMessage(update, "Користувач з цим номером телефону вже зараєструвався");
        }
        client.setChatid(id);
        controller.update(client);

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
        return createResponseMessage(update, controller.getByChatId(chatId).toString());
    }

    public SendMessage reset(Update update){
        int chatId = Integer.parseInt(update.getMessage().getChatId().toString());
        if(checkChatId(chatId)){
            return createResponseMessage(update, "ChatId does not exist");
        }
        Client client = controller.getByChatId(chatId);
        client.setChatid(0);
        controller.update(client);
        return createResponseMessage(update, "ChatId reset completed");
    }

    private SendMessage createResponseMessage(Update update, String text){
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(text);

        return message;
    }

    private boolean checkPhone(String phone){
        Client client = controller.getAbsolutelyAll().stream()
                .filter(n -> n.getPhone().equals(phone))
                .findFirst()
                .orElse(null);
        return Objects.nonNull(client);
    }

    private boolean checkChatId(int id){
        Client client = controller.getAbsolutelyAll().stream()
                .filter(n -> n.getChatid() == id)
                .findFirst()
                .orElse(null);
        return Objects.isNull(client);
    }

    private boolean chatIdPresent(Client client){
        return client.getChatid() != 0;
    }

}
