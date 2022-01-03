package com.example.demo.model;

public class CustomerFactory {

    private String name;
    private String phone;
    private Integer count;
    private Boolean active;
    private Integer chatId;
    private Boolean notification = false;

    public static Customer getCustomer(Client client){
        Customer customer = new Customer();
        customer.setName(client.getName());
        customer.setPhone(client.getPhone());
        customer.setCount(client.getCount());
        customer.setActive(client.isActive());
        customer.setChatId(client.getChatid());
        customer.setNotification(client.isNotification());
        return customer;
    }
}
