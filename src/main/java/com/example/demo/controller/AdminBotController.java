package com.example.demo.controller;

import com.example.demo.bots.UserInfoBot;
import com.example.demo.model.Customer;
import com.example.demo.model.Payment;
import com.example.demo.model.PaymentFactory;
import com.example.demo.model.VisitFactory;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.VisitRepository;
import com.example.demo.repository.impl.CustomerRepositoryImpl;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminBotController extends CrudController{

    private final PaymentRepository paymentRepository;
    private final VisitRepository visitRepository;
    private final UserInfoBot userInfoBot;
    private Timer timer = new Timer();

    public AdminBotController(CustomerRepositoryImpl repository, PaymentRepository paymentRepository, VisitRepository visitRepository, UserInfoBot userInfoBot) {
        super(repository);
        this.paymentRepository = paymentRepository;
        this.visitRepository = visitRepository;
        this.userInfoBot = userInfoBot;
    }

    @Override
    public Customer createNew(Customer customer) {
        updateTimer();
        return super.createNew(customer);
    }

    public Customer addPayment(LocalDate payDay, int id){
        Customer customer = getById(id);
        customer.setNotification(false);

        //if trainings started already
        if(customer.getName().contains("(!)")){
            customer.setName(customer.getName().replace("(!)", ""));

            sendToUsersInfoBot(customer, "❗Додано 10 тренувань❗\n" +
                    "Нагадую що тренування дійсні\n" +
                    "Від: " +customer.getLastPayment().getPayday().format(DateTimeFormatter.ofPattern("dd.MM")) + "\n" +
                    "До: " + customer.getLastPayment().getLastDay().format(DateTimeFormatter.ofPattern("dd.MM")));

            return update(customer);
        }

        //if everything okay, add new 10 trainings
        paymentRepository.save(PaymentFactory.createPayment(id, payDay));
        visitRepository.save(VisitFactory.createVisit(id, LocalDate.now()));
        customer.setCount(1);

        sendToUsersInfoBot(customer, "❗Додано 10 тренувань❗\nНагадую, що тренування дійсні\n" +
                "Від: " +payDay.format(DateTimeFormatter.ofPattern("dd.MM")) + "\n" +
                "До: " + payDay.plusDays(35).format(DateTimeFormatter.ofPattern("dd.MM")));

        updateTimer();
        return update(customer);
    }

    public String addVisit(List<String> idList, String date){
        date = date + ".2022";

        List<Customer> customerList = idList.stream()
                .map(Integer::parseInt)
                .map(this::getById)
                .collect(Collectors.toList());

        LocalDate visitDate;
        try {
            visitDate = new SimpleDateFormat("dd.MM.yyyy")
                    .parse(date)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        } catch (ParseException e) {
            e.printStackTrace();
            return "Problem with date";
        }

        StringBuilder result = new StringBuilder(date + "\n");

        for(Customer currentCustomer : customerList) {
            boolean visitExist = currentCustomer.getVisits().stream().anyMatch(visit -> visit.getDate().equals(visitDate));

            if(!visitExist){
                if (currentCustomer.getCount() == 10) {
                    currentCustomer.setCount(1);
                    currentCustomer.setName(currentCustomer.getName() + "(!)");
                    visitRepository.save(VisitFactory.createVisit(currentCustomer.getId(), visitDate));
                    paymentRepository.save(PaymentFactory.createPayment(currentCustomer.getId(), visitDate));
                } else {
                    currentCustomer.setCount(currentCustomer.getCount() + 1);
                    visitRepository.save(VisitFactory.createVisit(currentCustomer.getId(), visitDate));
                }
                sendToUsersInfoBot(currentCustomer, randomVisitMessage(date));
                result.append(update(currentCustomer).getName()).append("\n");
            } else {
                result.append(update(currentCustomer).getName()).append("(++)").append("\n");
            }
        }
        updateTimer();
        return result.toString();
    }

    public List<Customer> paySoon() {
        return repository.paySoon();
    }

    public List<Customer> allConnectedToBot(){
        return getAbsolutelyAll().stream()
                .filter(c -> c.getChatId() != 0)
                .collect(Collectors.toList());
    }

    public Customer updateCountById(int id, int count){
        Customer client = getById(id);
        client.setCount(count);
        return update(client);
    }

    public Customer updatePhoneById(int id, String phone){
        Customer client = getById(id);
        client.setPhone(phone);
        return update(client);
    }
    
    public Customer updateNameById(int id, String name){
        Customer client = getById(id);
        client.setName(name);
        return update(client);
    }

    public void notActive(int id){
        Customer client = getById(id);
        client.setActive(false);
        client.setNotification(true);
        update(client);
        updateTimer();
    }

    public void activeAgain(int id){
        Customer client = getById(id);
        client.setActive(true);
        client.setNotification(false);
        update(client);
        updateTimer();
    }

    public void deleteById(int id){
        repository.deleteById(id);
        updateTimer();
    }

    public void sendToUsersInfoBot(Customer customer, String text){
        if(customer.getChatId() == 0){
            return;
        }
        userInfoBot.messageToUser(customer.getChatId(), text);
    }

    public void sendToAllUsers(String text){
        getAll().stream()
                .filter(c -> !c.getChatId().equals(0))
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
        getAll().forEach(customer -> {
            if(customer.isActive() && !customer.isNotification()) {

                LocalDate weekBeforeDay =
                        customer.getLastPayment()
                                .getLastDay()
                                .minusDays(5);

                Calendar weekBeforeNotification =
                        GregorianCalendar.from(weekBeforeDay.atStartOfDay(ZoneId.systemDefault()));
                weekBeforeNotification.set(Calendar.HOUR_OF_DAY, 10);

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        sendToUsersInfoBot(customer, "Тренування закінчуються "
                                + customer.getLastPayment().getLastDay()
                                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                + " \uD83D\uDCC5\uD83D\uDE35");
                        customer.setNotification(true);
                        update(customer);
                    }
                };

                timer.schedule(task, weekBeforeNotification.getTime());
            }
        });
    }
}