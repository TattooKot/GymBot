package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.repository.impl.CustomerRepositoryImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CrudController {
    protected final CustomerRepositoryImpl customerRepository;

    public CrudController(CustomerRepositoryImpl customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAll(){
        List<Customer> customerList = customerRepository.getAll();
        customerList.removeIf(c -> !c.isActive());
        return customerList;
    }

    public List<Customer> getAbsolutelyAll(){
        return customerRepository.getAll();
    }

    public boolean checkById(int id){
        return customerRepository.checkById(id);
    }

    public Customer getById(int id){
        return customerRepository.getById(id);
    }

    public Customer createNew(Customer customer){
        return customerRepository.create(customer);
    }

    public Customer update(Customer customer) {
        return customerRepository.update(customer);
    }
}
