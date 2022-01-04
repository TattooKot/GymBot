package com.example.demo.model;

import java.time.LocalDate;

public class PaymentFactory {

    public static Payment createPayment(Integer customerId, LocalDate payday){
        Customer customer = new Customer();
        customer.setId(customerId);

        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setPayday(payday);
        payment.setLastDay(payday.plusDays(35));

        return payment;
    }
}
