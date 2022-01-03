package com.example.demo.model;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

@Table(name = "clients")
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "phone", length = 10)
    private String phone;

    @Column(name = "payday")
    private LocalDate payday;

    @Column(name = "lastday")
    private LocalDate lastday;

    @Column(name = "count")
    private Integer count;

    @Column(name = "frequency")
    private String frequency;

    @Column(name = "active")
    private boolean active;

    @Column(name = "chatid")
    private Integer chatid;

    @Column(name = "notification")
    private boolean notification;

    public Client() {
        this.name = "";
        this.payday = LocalDate.parse(("01.01.1970"), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        this.lastday = LocalDate.parse(("01.02.1970"), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        this.active = true;
        this.count = 0;
        this.frequency = "";
        this.phone = "";
        this.chatid = 0;
        this.notification = false;
    }



    @Override
    public String toString() {

         StringBuilder result = new StringBuilder(name);

        if(!phone.isEmpty()) {
            result.append("\n" + "Телефон: ").append(phone);
        }

        result.append("\n\nОплата: ")
                .append(payday.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .append("\n")
                .append("До: ")
                .append(lastday.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

         if(lastday.isBefore(LocalDate.now())) {
             result.append("\n" + "(!)Час вийшов(!)");
         }

         if(count >= 8) {
             result.append("\n" + "К-сть: ").append(count).append("(!)");
         } else {
             result.append("\n" + "К-сть: ").append(count);
         }


         if(!frequency.isEmpty()){
             result.append("\n\n" + "Відвідування:  \n");

             ArrayList<String> dates = new ArrayList<>(Arrays.asList(frequency.split(",")));


             dates.stream().map(d -> {
                 try {
                     return new SimpleDateFormat("dd.MM").parse(d);
                 } catch (ParseException e) {
                     e.printStackTrace();
                 }
                 return null;
             }).sorted(Comparator.reverseOrder())
                     .forEach(d -> result.append(new SimpleDateFormat("dd.MM").format(d)).append("\n"));
         }
        return result.toString();
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public Integer getChatid() {
        return chatid;
    }

    public void setChatid(Integer chatId) {
        this.chatid = chatId;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public LocalDate getLastday() {
        return lastday;
    }

    public void setLastday(LocalDate lastday) {
        this.lastday = lastday;
    }

    public LocalDate getPayday() {
        return payday;
    }

    public void setPayday(LocalDate payday) {
        this.payday = payday;
        this.lastday = payday.plusDays(35);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}