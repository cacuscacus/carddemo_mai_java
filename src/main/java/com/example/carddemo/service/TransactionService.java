package com.example.carddemo.service;

import com.example.carddemo.model.Transaction;
import com.example.carddemo.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction getTransactionById(String id) {
        return transactionRepository.findById(id);
    }

    public Page<Transaction> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public Page<Transaction> searchTransactions(String transactionId, Pageable pageable) {
        if (transactionId != null && !transactionId.isEmpty()) {
            return transactionRepository.findByTransactionIdContaining(transactionId, pageable);
        } else {
            return transactionRepository.findAll(pageable);
        }
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(String id, Transaction transaction) {
        Transaction existingTransaction = transactionRepository.findById(id);
        if (existingTransaction != null) {
            return transactionRepository.save(transaction);
        } else {
            return null;
        }
    }

    public void deleteTransaction(String id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByOriginalTimestampBetween(startDate, endDate);
    }
}