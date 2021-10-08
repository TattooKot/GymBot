package com.example.demo.view;

import com.example.demo.controller.UserInfoController;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UserInfoView {

    private final UserInfoController controller;

    public UserInfoView(UserInfoController controller) {
        this.controller = controller;
    }

    public SendMessage connect(Update update){
        String phone = update.getMessage().getText();
        String id = update.getMessage().getChatId().toString();
        return createResponseMessage(update, "Phone: " + phone + " will connect to chat id: " + id);
    }

    private SendMessage createResponseMessage(Update update, String text){
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(text);

        return message;
    }
}
