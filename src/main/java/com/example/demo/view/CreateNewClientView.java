package com.example.demo.view;

import com.example.demo.controller.ClientController;
import com.example.demo.model.Client;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;

@Component
public class CreateNewClientView {

    private final ClientController controller;
    private boolean wantCreate = false;
    private boolean nameAdded;
    private boolean phoneAdded;
    private Client client;

    public CreateNewClientView(ClientController controller) {
        this.controller = controller;
    }

    public SendMessage createNew(Update update){
        client = new Client();
        wantCreate = true;
        return createResponseMessage(update, "Lets start! Enter name:");
    }

    public SendMessage process(Update update){
        String data = update.getMessage().getText();
        if(!nameAdded){
            client.setName(data);
            nameAdded = true;
            return createResponseMessage(update, "Name added: " + client.getName()
                    + "\nEnter phone: ");
        }
        if(!phoneAdded){
            if(!data.matches("^\\d{10}$")){
                return createResponseMessage(update, "Phone has only 10 numbers");
            }else {
                client.setPhone(data);
                phoneAdded = true;
                return createResponseMessage(update, "Phone added\nIs payday today?(+/-)");
            }
        }
        done();
        if(data.equals("+")){
            Client created = controller.createNew(client);
            int id = created.getId();
            controller.addPayment(LocalDate.now(), id);
            return createResponseMessage(update, "Client added:\n" + controller.getById(id));
        }
        return createResponseMessage(update, "Client added:\n" + controller.createNew(client));
    }

    private SendMessage createResponseMessage(Update update, String text){
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(text);

        return message;
    }

    private void done(){
        nameAdded = false;
        phoneAdded = false;
        wantCreate = false;
    }

    public boolean isWantCreate() {
        return wantCreate;
    }
}
