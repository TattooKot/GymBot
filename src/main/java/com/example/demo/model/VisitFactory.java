package com.example.demo.model;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
@Component
public class VisitFactory {

    public static Visit createVisit(Integer customerId, LocalDate date){
        Customer customer = new Customer();
        customer.setId(customerId);

        Visit visit = new Visit();
        visit.setCustomer(customer);
        visit.setDate(date);

        return visit;
    }
}
