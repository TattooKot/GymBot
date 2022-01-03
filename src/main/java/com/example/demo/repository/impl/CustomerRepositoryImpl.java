package com.example.demo.repository.impl;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class CustomerRepositoryImpl {
    private final CustomerRepository customerRepository;

    public CustomerRepositoryImpl(@Lazy CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAll(){
        return getAllInternal();
    }

    public List<Customer> paySoon(){
        List<Customer> customerList = new ArrayList<>();
        getAllInternal().stream()
                .filter(Customer::isActive)
                .forEach(c ->
                {
                    if(c.getCount() >= 8
                            || (c.getLastPayment().getLastDay().minusDays(7).isBefore(LocalDate.now()))
                            || c.getName().contains("!"))
                    {
                        customerList.add(c);
                    }
                });
        return customerList;
    }

    public boolean checkById(int id){
        return customerRepository.existsById(id);
    }

    public Customer create (Customer customer){
        return customerRepository.save(customer);
    }

    public Customer getById(int id){
        return customerRepository.findById(id).orElse(null);
    }

    public Customer update(Customer customer){
        return customerRepository.save(customer);
    }

    public void deleteById(int id){
        customerRepository.deleteById(id);
    }

    private List<Customer> getAllInternal(){
        List<Customer> customerList = customerRepository.findAll();
        customerList.sort(Comparator.comparingInt(Customer::getId));
        return customerList;
    }
}
