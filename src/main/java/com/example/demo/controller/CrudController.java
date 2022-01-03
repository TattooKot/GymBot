package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.repository.impl.CustomerRepositoryImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CrudController {
    protected final CustomerRepositoryImpl repository;

    public CrudController(CustomerRepositoryImpl repository) {
        this.repository = repository;
    }

    public List<Customer> getAll(){
        List<Customer> customerList = repository.getAll();
        customerList.removeIf(c -> !c.isActive());
        return customerList;
    }

    public List<Customer> getAbsolutelyAll(){
        return repository.getAll();
    }

    public boolean checkById(int id){
        return repository.checkById(id);
    }

    public Customer getById(int id){
        return repository.getById(id);
    }

    public Customer createNew(Customer customer){
        return repository.create(customer);
    }

    public Customer update(Customer customer) {
        return repository.update(customer);
    }
}
