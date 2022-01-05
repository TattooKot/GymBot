package com.example.demo.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Table(name = "customers")
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone", nullable = false, length = 10)
    private String phone;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "chat_id", nullable = false)
    private Integer chatId;

    @Column(name = "notification", nullable = false)
    private Boolean notification = false;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "customer")
    private List<Visit> visits;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "customer")
    private List<Payment> payments;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(name);

        if(!phone.isEmpty()) {
            result.append("\n" + "Телефон: ").append(phone);
        }

        Payment lastest = getLastPayment();

        result.append("\n\nОплата: ")
                .append(lastest.getPayday().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .append("\n")
                .append("До: ")
                .append(lastest.getLastDay().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        if(lastest.getLastDay().isBefore(LocalDate.now())) {
            result.append("\n" + "(!)Час вийшов(!)");
        }

        if(count >= 8) {
            result.append("\n" + "К-сть: ").append(count).append("(!)");
        } else {
            result.append("\n" + "К-сть: ").append(count);
        }

        if(visits.size()!=0){
            result.append("\n\n" + "Відвідування:  \n");

            visits.sort((v1,v2) -> v1.getDate().isBefore(v2.getDate()) ? 1 : v1.getDate().equals(v2.getDate()) ? 0 : -1);

            for(Visit visit : visits){
                result.append(visit.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))).append("\n");
            }
        }
        return result.toString();
    }

    public Payment getLastPayment(){
        return payments.stream()
                .sorted(Comparator.comparingInt(Payment::getId))
                .collect(Collectors.toList())
                .get(payments.size()-1);
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }

    public Boolean isNotification() {
        return notification;
    }

    public void setNotification(Boolean notification) {
        this.notification = notification;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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

    public void setId(Integer id) {
        this.id = id;
    }
}