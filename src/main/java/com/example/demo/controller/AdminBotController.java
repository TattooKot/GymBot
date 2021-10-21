package com.example.demo.controller;

import com.example.demo.bots.UserInfoBot;
import com.example.demo.model.Client;
import com.example.demo.repository.ClientRepositoryImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminBotController extends CrudController{

    private final UserInfoBot userInfoBot;
    private Timer timer = new Timer();

    public AdminBotController(ClientRepositoryImpl repository, UserInfoBot userInfoBot) {
        super(repository);
        this.userInfoBot = userInfoBot;
    }

    @Override
    public Client createNew(Client client) {
        updateTimer();
        return super.createNew(client);
    }

    public Client addPayment(LocalDate payDay, int id){
        Client client = getById(id);
        client.setNotification(false);
        updateTimer();

        //if trainings started already
        if(client.getName().contains("(!)")){
            client.setName(client.getName().replace("(!)", ""));
            sendToUsersInfoBot(client, "❗Додано 10 тренувань❗\n" +
                    "Нагадую що тренування дійсні\n" +
                    "Від: " +client.getPayday().format(DateTimeFormatter.ofPattern("dd.MM")) + "\n" +
                    "До: " + client.getLastday().format(DateTimeFormatter.ofPattern("dd.MM")));
            return update(client);
        }

        //if everything okay, add new 10 trainings
        client.setPayday(payDay);
        client.setCount(1);
        client.setFrequency(payDay.format(DateTimeFormatter.ofPattern("dd.MM"))+ "(payday)," + client.getFrequency());
        sendToUsersInfoBot(client, "❗Додано 10 тренувань❗\nНагадую, що тренування дійсні\n" +
                "Від: " +payDay.format(DateTimeFormatter.ofPattern("dd.MM")) + "\n" +
                "До: " + client.getLastday().format(DateTimeFormatter.ofPattern("dd.MM")));

        return update(client);
    }

    public String addVisit(List<String> stringIdList, String date){
        List<Client> clientList = stringIdList.stream()
                .map(Integer::parseInt)
                .map(this::getById)
                .collect(Collectors.toList());

        StringBuilder result = new StringBuilder(date + "\n");

        for(Client currentClient : clientList) {

            if (!currentClient.getFrequency().contains(date)) {
                if (currentClient.getCount() == 10) {
                    currentClient.setFrequency(date + "(!)," + currentClient.getFrequency());
                    currentClient.setCount(1);
                    currentClient.setName(currentClient.getName() + "(!)");
                    currentClient.setPayday(LocalDate.now());
                } else {
                    currentClient.setFrequency(date + "," + currentClient.getFrequency());
                    currentClient.setCount(currentClient.getCount() + 1);
                }

                sendToUsersInfoBot(currentClient, randomVisitMessage(date));
                result.append(update(currentClient).getName()).append("\n");

            } else result.append(update(currentClient).getName()).append("(++)").append("\n");
        }
        updateTimer();
        return result.toString();
    }

    public List<Client> paySoon() {
        return repository.paySoon();
    }

    public List<Client> allConnectedToBot(){
        return getAbsolutelyAll().stream()
                .filter(c -> c.getChatid() != 0)
                .collect(Collectors.toList());
    }

    public void notActive(int id){
        Client client = getById(id);
        client.setActive(false);
        client.setNotification(true);
        update(client);
        updateTimer();
    }

    public void activeAgain(int id){
        Client client = getById(id);
        client.setActive(true);
        client.setNotification(false);
        update(client);
        updateTimer();
    }

    public void deleteById(int id){
        repository.deleteById(id);
        updateTimer();
    }

    public void sendToUsersInfoBot(Client client, String text){
        if(client.getChatid() == 0){
            return;
        }
        userInfoBot.messageToUser(client.getChatid(), text);
    }

    public void sendToAllUsers(String text){
        getAll().stream()
                .filter(c -> !c.getChatid().equals(0))
                .forEach(c -> sendToUsersInfoBot(c,text));
    }

    private String randomVisitMessage(String date){
        List<String> messages = new ArrayList<>();
        messages.add("Тренування %s закінчено! \uD83D\uDE0E\n" + "Машина, йомайо! \uD83D\uDE04");
        messages.add("Тренування %s - Done✔️\n" + "Молодець! \uD83D\uDD25\n" + "Тільки не вмри\uD83E\uDD15");
        messages.add("Думаю тренування %s було на 5 з 10\uD83E\uDDD0\n" + "Головне поїж! \uD83C\uDF2E\uD83E\uDDC0\uD83C\uDF5C\uD83C\uDF6A\uD83C\uDF69\uD83C\uDF70");
        messages.add("%s \uD83D\uDCC5\n" + "Харооош! \uD83D\uDE0E\uD83E\uDD1C\uD83C\uDFFB\uD83E\uDD1B\uD83C\uDFFB\n" + "А тепер їсти спати\uD83C\uDF5C \uD83D\uDE34");
        messages.add("%s \uD83D\uDCC5\n" + "Але ж то вже машина! \uD83D\uDE9C\n" + "Анука не горбся\uD83D\uDE2C\n" + "Рівно йди! \uD83D\uDEB6\u200D♀️\uD83D\uDEB6\u200D♂️\uD83D\uDD7A\uD83D\uDC83\n");
        return String.format(messages.get((int) (Math.random() * messages.size())), date);
    }

    private void updateTimer(){
        timer.cancel();
        timer.purge();
        timer = new Timer();
        paymentNotification(timer);
    }

    private void paymentNotification(Timer timer){

        getAll().forEach(client -> {
            if(client.isActive() && !client.isNotification()) {
                LocalDate weekBeforeDay = client.getLastday().minusDays(5);
                Date weekBeforeNotification = Date.from(weekBeforeDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
                weekBeforeNotification.setHours(10);
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        sendToUsersInfoBot(client, "Тренування закінчуються "
                                + client.getLastday().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                + " \uD83D\uDCC5\uD83D\uDE35");
                        client.setNotification(true);
                        update(client);
                    }
                };
                timer.schedule(task, weekBeforeNotification);
            }
        });

    }

}