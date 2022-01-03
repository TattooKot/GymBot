package com.example.demo.model;

import com.example.demo.model.Customer;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "payments")
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "payday", nullable = false)
    private LocalDate payday;

    @Column(name = "last_day", nullable = false)
    private LocalDate lastDay;

    public Payment() {
    }

    public Payment(Integer customerId, LocalDate payday, LocalDate lastDay) {
        this.customer = new Customer();
        this.customer.setId(customerId);
        this.payday = payday;
        this.lastDay = lastDay;
    }

    @Override
    public String toString() {
        return "Оплата: " + payday + "\n" +
                "До: " + lastDay;
    }

    public LocalDate getLastDay() {
        return lastDay;
    }

    public void setLastDay(LocalDate lastDay) {
        this.lastDay = lastDay;
    }

    public LocalDate getPayday() {
        return payday;
    }

    public void setPayday(LocalDate payday) {
        this.payday = payday;
    }

    public Integer getCustomerId() {
        return this.customer.getId();
    }

    public void setCustomerId(Integer customerId) {
        if(this.customer == null){
            this.customer = new Customer();
        }

        this.customer.setId(customerId);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}