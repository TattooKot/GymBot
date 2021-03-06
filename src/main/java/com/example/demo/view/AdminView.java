package com.example.demo.view;


import com.example.demo.controller.AdminBotController;
import com.example.demo.model.Customer;
import com.example.demo.model.Fields;
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

    private final AdminBotController adminBotController;

    public AdminView(AdminBotController adminBotController) {
        this.adminBotController = adminBotController;
    }

    public SendMessage start(Update update){
        String result = "All commands:\n" + "\n" +
                "/get_all - all active" + "\n" +
                "/get_all_all - get all" + "\n" +
                "/pay_soon - pay soon" + "\n" +
                "/create - create new Customer" + "\n" +
                "/delete {id} - delete by id" + "\n" +
                "/send {text} - send all" + "\n" +
                "/all_connected - all connected to bot" + "\n" +
                "{id} - get user by id" + "\n" +
                "{date} {id..} - add visit" + "\n" +
                "Add pay {date} {id} - add payment" + "\n" +
                "Delete {id} - set active false" + "\n" +
                "Active {id} - set active true" + "\n" +
                "Name {id} {name} - update name" + "\n" +
                "Phone {id} {phone} - update phone" + "\n" +
                "Count {id} {count} - update count" + "\n" +
                "Pay {id} - customer payments" + "\n";

        return createResponseMessage(update, result);
    }

    public SendMessage getAll(Update update){
        List<Customer> customerList = adminBotController.getAll();
        return createResponseMessage(update, createStringFromListOfCustomers(customerList));
    }

    public SendMessage paySoon(Update update){
        List<Customer> customerList = adminBotController.paySoon();
        return createResponseMessage(update, createStringFromListOfCustomers(customerList));
    }

    public SendMessage getById(Update update){
        String request = update.getMessage().getText();
        int id = checkId(request);
        if(id == -1){
            return createResponseMessage(update, "Id ???? ????????????????, ?????????????? ???? ??????! \n");
        }

        return createResponseMessage(update, adminBotController.getById(id).toString());
    }

    public SendMessage customerPayments(Update update) {
        String request = update.getMessage().getText().replace("Pay", "").trim();

        int id = checkId(request);
        if(id == -1){
            return createResponseMessage(update, "Id ???? ????????????????, ?????????????? ???? ??????! \n");
        }

        return createResponseMessage(update, adminBotController.getCustomerPayments(id));
    }

    public SendMessage notActive(Update update){
        String request = update.getMessage().getText().replace("Delete", "").trim();

        int id = checkId(request);
        if(id == -1){
            return createResponseMessage(update, "???????????????????????? id");
        }
        adminBotController.notActive(id);
        return createResponseMessage(update, id + " ????????????????????????");
    }

    public SendMessage activeAgain(Update update){
        String request = update.getMessage().getText().replace("Active", "").trim();

        int id = checkId(request);
        if(id == -1){
            return createResponseMessage(update, "???????????????????????? id");
        }
        adminBotController.activeAgain(id);
        return createResponseMessage(update, id + " ????????????????????");
    }

    public SendMessage addVisit(Update update) {
        String request = update.getMessage().getText();
        String date = request.substring(0,request.indexOf(" "));

        List<String> customers = new ArrayList<>(
                List.of(request.substring(request.indexOf(" "))
                        .trim()
                        .split(" ")));

        for(String customer : customers){
            int id = checkId(customer);
            if(id == -1){
                return createResponseMessage(update, "???????????????????????? Id: " + id);
            }
        }

        return createResponseMessage(update, adminBotController.addVisit(customers, date)
                + "\n" + createStringFromListOfCustomers(adminBotController.getAll()));
    }

    public SendMessage addPayment(Update update) {
        return updateCustomerInfo(update, Fields.PAYMENT);
    }

    public SendMessage updatePhone(Update update){
        return updateCustomerInfo(update, Fields.PHONE);
    }

    public SendMessage updateCount(Update update){
        return updateCustomerInfo(update, Fields.COUNT);
    }

    public SendMessage updateName(Update update){
        return updateCustomerInfo(update, Fields.NAME);
    }

    public SendMessage getAbsolutelyAll(Update update){
        return createResponseMessage(update, createStringFromListOfCustomers(adminBotController.getAbsolutelyAll()));
    }

    public SendMessage deleteById(Update update){
        String data = update.getMessage().getText().replace("/delete", "").trim();
        int id = checkId(data);
        if(id == -1){
            return createResponseMessage(update, "???????????????????????? id");
        }
        adminBotController.deleteById(id);
        return createResponseMessage(update, "???????????????????? ??????????????????: " + id);
    }

    public SendMessage allConnectedToBot(Update update){
        return createResponseMessage(update, createStringFromListOfCustomers(adminBotController.allConnectedToBot()));
    }

    public SendMessage sendToAllUsers(Update update){
        String text = update.getMessage().getText().trim();
        if(text.equals("/send")){
            return createResponseMessage(update, "?????????? /send ???????????? ????????????????????????");
        }
        text = text.replace("/send", "").trim();

        adminBotController.sendToAllUsers(text);
        return createResponseMessage(update, "???????????????????????? ??????????????????");
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
        if(!adminBotController.checkById(id)) {
            return -1;
        }
        return id;
    }

    private String createStringFromListOfCustomers(List<Customer> customerList){
        StringBuilder stringBuilder = new StringBuilder();

        for(Customer customer : customerList) {
            if (customer.getCount() >= 8) {
                stringBuilder.append(String.format("%d.%s: %d(!)",
                        customer.getId(), customer.getName(), customer.getCount()));

            } else if (customer.getLastPayment().getLastDay().minusDays(7).isBefore(LocalDate.now())) {
                stringBuilder.append(String.format("%d.%s: %d(t)",
                        customer.getId(), customer.getName(), customer.getCount()));
            } else {
                stringBuilder.append(String.format("%d.%s: %d",
                        customer.getId(), customer.getName(), customer.getCount()));
            }

            if(!customer.isActive()){
                stringBuilder.append("(N/A)");
            }
            stringBuilder.append("\n");
        }

        stringBuilder.append("\n").append("Count: ").append(customerList.size());

        return stringBuilder.toString();
    }

    private String[] getDataArrayFromRequest(Update update, String fieldName){
        String request = update.getMessage().getText().replace(fieldName, "").trim();

        if(!request.contains(" ")){
            return null;
        }

        return request.split(" ");
    }

    private SendMessage updateCustomerInfo(Update update, Fields field){
        String[] data = getDataArrayFromRequest(update,field.toString());

        if(Objects.isNull(data)){
            return createResponseMessage(update, "Bad arguments");
        }

        int id = checkId(data[0]);
        String value = data[1];

        if(id == -1){
            return createResponseMessage(update, "Id does not exist");
        }

        if (field == Fields.NAME) {
            return createResponseMessage(update, "Name updated:\n" + adminBotController.updateNameById(id, value));
        }

        if (field == Fields.PHONE) {
            if (!value.matches("^\\d{10}$")) {
                return createResponseMessage(update, "Bad phone number");
            }
            return createResponseMessage(update, "Phone updated\n\n"
                    + adminBotController.updatePhoneById(id, value));
        }

        if (field == Fields.COUNT) {
            if (!value.matches("^\\d{1,2}$")
                    || Integer.parseInt(data[1]) > 10
                    || Integer.parseInt(data[1]) < 0) {
                return createResponseMessage(update, "Bad count");
            }
            return createResponseMessage(update, "Count updated:\n"
                    + adminBotController.updateCountById(id, Integer.parseInt(value)));
        }

        if (field == Fields.PAYMENT) {
            value += ".2022";
            LocalDate payDay = null;
            try {
                payDay = new SimpleDateFormat("dd.MM.yyyy")
                        .parse(value)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Customer customer = adminBotController.addPayment(payDay, id);
            return createResponseMessage(update, "Payment added for: " + customer.getName());
        }
        return createResponseMessage(update, "ERROR in updateCustomerInfo " + id + " " + value);
    }
}