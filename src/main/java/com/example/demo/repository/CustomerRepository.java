package com.example.demo.repository;

import com.example.demo.model.Customer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Override
    @EntityGraph(attributePaths = {"visits", "payments"})
    @NonNull
    Optional<Customer> findById(@NonNull Integer integer);
}
