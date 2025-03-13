package com.example.carddemo.repository;

import com.example.carddemo.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Importos for future implementation of JpaRepository with AWS Aurora
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;

public interface TransactionRepository  {
    Transaction findById(String id);
    Page<Transaction> findAll(Pageable pageable);
    Page<Transaction> findByTransactionIdContaining(String transactionId, Pageable pageable);
    List<Transaction> findByOriginalTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    Transaction save(Transaction transaction);
    void deleteById(String id);
}