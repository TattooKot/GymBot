package com.example.demo.model;

public class CustomerFactory {

    public static Customer createCustomer(){
        Customer customer = new Customer();
        customer.setCount(0);
        customer.setChatId(0);
        customer.setActive(true);
        customer.setNotification(false);
        return customer;
    }
}
