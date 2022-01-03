package com.example.demo.model;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "visits")
@Entity
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Override
    public String toString() {
        return date.toString();
    }

    public Visit() {
    }

    public Visit(Integer customerId, LocalDate date) {
        this.customer = new Customer();
        this.customer.setId(customerId);
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}