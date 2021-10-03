package com.example.demo;

import com.example.demo.view.ClientView;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {

    private final ClientView view;

    public Bot(ClientView view) {
        this.view = view;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String request = update.getMessage().getText();

            if(request.equals("/get_all")) {
                send(view.getAll(update));
            }
            if(request.equals("/pay_soon")) {
                send(view.paySoon(update));
            }
            if(request.contains("Add pay")) {
                send(view.addPayment(update));
            }
            if(request.contains("Delete")) {
                send(view.notActive(update));
            }
            if(request.matches("^\\d{1,2}$")) {
                send(view.getById(update));
            }
            if(request.matches("\\d{1,2}\\.\\d{2}.*")) {
                send(view.addVisit(update));
            }
            if(request.equals("04k0")) {
                send(view.bestFrau(update));
            }
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
