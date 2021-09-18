package com.example.demo;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {

    private final ClientModel controller;

    public Bot(ClientModel controller) {
        this.controller = controller;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String request = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if(request.equals("/get_all"))
                send(controller.getAll(update));

            if(request.toLowerCase().contains("id") || request.matches("^\\d{1,2}$"))
                send(controller.getById(update));

            if(request.equals("04k0"))
                send(controller.bestFrau(update));


        }

    }

    public void send(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getBotUsername() {
        return "Tk_testBot";
    }

    @Override
    public String getBotToken() {
        return "1972022499:AAFDNdVth4-VMg-RkwyrX2zug6UWAcuQAvE";
    }
}
