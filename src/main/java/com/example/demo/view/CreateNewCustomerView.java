package com.example.demo.view;

import com.example.demo.controller.AdminBotController;
import com.example.demo.model.Customer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;

@Component
public class CreateNewCustomerView {

    private final AdminBotController controller;
    private boolean wantCreate = false;
    private boolean nameAdded;
    private boolean phoneAdded;
    private Customer customer;

    public CreateNewCustomerView(AdminBotController controller) {
        this.controller = controller;
    }

    public SendMessage createNew(Update update){
        customer = new Customer();
        wantCreate = true;
        return createResponseMessage(update, "Lets start! Enter name:");
    }

    public SendMessage process(Update update){
        String data = update.getMessage().getText();
        if(!nameAdded){
            customer.setName(data);
            nameAdded = true;
            return createResponseMessage(update, "Name added: " + customer.getName()
                    + "\nEnter phone: ");
        }
        if(!phoneAdded){
            if(data.equals("-")){
                phoneAdded = true;
            }else if(!data.matches("^\\d{10}$")){
                return createResponseMessage(update, "Phone has only 10 numbers");
            }else {
                customer.setPhone(data);
                phoneAdded = true;
            }
            return createResponseMessage(update, "Phone added\nIs payday today?(+/-)");
        }
        done();
        if(data.equals("+")){
            Customer created = controller.createNew(customer);
            int id = created.getId();
            controller.addPayment(LocalDate.now(), id);
            return createResponseMessage(update, "Customer added:\n" + controller.getById(id));
        }
        return createResponseMessage(update, "Customer added:\n" + controller.createNew(customer));
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

    private void done(){
        nameAdded = false;
        phoneAdded = false;
        wantCreate = false;
    }

    public boolean isWantCreate() {
        return wantCreate;
    }
}
