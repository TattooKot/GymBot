package com.example.demo.bots;

import com.example.demo.view.UserInfoView;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class UserInfoBot extends TelegramLongPollingBot {

    private final UserInfoView view;

    public UserInfoBot(UserInfoView view) {
        this.view = view;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String request = update.getMessage().getText();

            if(request.equals("/start")){
                send(view.start(update));
            }
            if(request.matches("^\\d{10}$")){
                send(view.connect(update));
            }
            if(request.equals("/sho_tam")){
                send(view.info(update));
            }
            if(request.equals("/info")){
                send(view.rules(update));
            }
            if(request.equals("/reset")){
                send(view.reset(update));
            }
            if(request.equals("/help")){
                send(view.help(update));
            }
        }

    }

    public void messageToUser(int chatId, String text){
        String disclaimer = "Привіт \uD83D\uDD90️\uD83D\uDE0A\n" +
                "Це повідомлення створено автоматично, і надіслане всім хто підключений до боту\uD83E\uDD16\n" +
                "Відповідати на нього не треба ❌\n" +
                "\n";
        SendMessage message = new SendMessage(String.valueOf(chatId), disclaimer + text);
        send(message);
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
        return "Tk_info_bot";
    }

    @Override
    public String getBotToken() {
        return "1969722356:AAG3wGjYd-c0pjhcEYLhsJ3vmyL9wQWEtHk";
    }
}
