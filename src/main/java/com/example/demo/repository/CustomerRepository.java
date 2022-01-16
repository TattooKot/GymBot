package com.example.demo.repository;

import com.example.demo.model.Customer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Override
    @EntityGraph(attributePaths = {"payments", "visits"}, type = EntityGraph.EntityGraphType.LOAD)
    @NonNull
    Optional<Customer> findById(@NonNull Integer id);

    @EntityGraph(attributePaths = {"visits", "payments"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Customer> findByChatId(Integer chatId);
}
