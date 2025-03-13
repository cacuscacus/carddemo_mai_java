package com.example.carddemo.repository;

import com.example.carddemo.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Repository
public class MockTransactionRepository implements TransactionRepository {

    private final Map<String, Transaction> transactions = new HashMap<>();

    public MockTransactionRepository() {
        // Existing transactions
        addTransaction("1234567890123456", "1111222233334444", "CD", "FOOD", "POS", "Grocery Store Purchase", new BigDecimal("75.25"), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), "MERCH123", "Super Groceries", "Anytown", "12345");
        addTransaction("9876543210987654", "5555666677778888", "DB", "UTIL", "WEB", "Electricity Bill Payment", new BigDecimal("120.50"), LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4), "POWER4U", "Power Company", "Cityville", "67890");

        // Add 10 more mocked transactions
        addTransaction(generateId(), "1111222233334444", "CD", "FOOD", "POS", "Restaurant Meal", new BigDecimal("45.80"), LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), "REST001", "The Tasty Spoon", "Anytown", "12345");
        addTransaction(generateId(), "5555666677778888", "CR", "PAYM", "WEB", "Credit Card Payment", new BigDecimal("200.00"), LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1), "BANK001", "My Bank", "Online", "00000");
        addTransaction(generateId(), "9999888877776666", "DB", "SHOP", "POS", "Clothing Purchase", new BigDecimal("85.99"), LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(3), "CLOTHES1", "Fashion Central", "Anytown", "12345");
        addTransaction(generateId(), "1111222233334444", "CD", "FOOD", "POS", "Coffee Shop", new BigDecimal("12.50"), LocalDateTime.now().minusDays(1), LocalDateTime.now(), "COFFEE1", "Morning Brew", "Anytown", "12345");
        addTransaction(generateId(), "5555666677778888", "DB", "UTIL", "WEB", "Gas Bill Payment", new BigDecimal("65.20"), LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(9), "GASCO", "Gas Company", "Cityville", "67890");
        addTransaction(generateId(), "9999888877776666", "DB", "SHOP", "POS", "Bookstore Purchase", new BigDecimal("35.75"), LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(6), "BOOKS1", "Page Turners", "Anytown", "12345");
        addTransaction(generateId(), "1111222233334444", "CD", "ENT", "POS", "Movie Tickets", new BigDecimal("28.00"), LocalDateTime.now().minusDays(8), LocalDateTime.now().minusDays(7), "CINEMA1", "City Cinema", "Anytown", "12345");
        addTransaction(generateId(), "5555666677778888", "DB", "UTIL", "WEB", "Internet Bill Payment", new BigDecimal("55.00"), LocalDateTime.now().minusDays(12), LocalDateTime.now().minusDays(11), "INTERNET1", "FastNet", "Cityville", "67890");
        addTransaction(generateId(), "9999888877776666", "CD", "TRAV", "WEB", "Airline Ticket", new BigDecimal("350.00"), LocalDateTime.now().minusDays(15), LocalDateTime.now().minusDays(14), "AIRLINE1", "Sky High Airlines", "Online", "00000");
        addTransaction(generateId(), "1111222233334444", "DB", "HOME", "POS", "Home Improvement", new BigDecimal("120.30"), LocalDateTime.now().minusDays(9), LocalDateTime.now().minusDays(8), "HOMEIMP1", "Build It Better", "Anytown", "12345");
        addTransaction(generateId(), "1111222233334444", "DB", "HOME", "POS", "Home Improvement", new BigDecimal("120.30"), LocalDateTime.now().minusDays(40), LocalDateTime.now().minusDays(39), "HOMEIMP1", "Build It Better", "Anytown", "12345");
        addTransaction(generateId(), "1111222233334444", "DB", "HOME", "POS", "Home Improvement", new BigDecimal("120.30"), LocalDateTime.now().minusDays(55), LocalDateTime.now().minusDays(54), "HOMEIMP1", "Build It Better", "Anytown", "12345");
        addTransaction(generateId(), "1111222233334444", "DB", "HOME", "POS", "Home Improvement", new BigDecimal("120.30"), LocalDateTime.now().minusDays(78), LocalDateTime.now().minusDays(77), "HOMEIMP1", "Build It Better", "Anytown", "12345");
    }
    private void addTransaction(String id, String cardNumber, String typeCode, String categoryCode, String source,
                                String description, BigDecimal amount, LocalDateTime originalTimestamp,
                                LocalDateTime processedTimestamp, String merchantId, String merchantName,
                                String merchantCity, String merchantZip) {
         Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setCardNumber(cardNumber);
        transaction.setTypeCode(typeCode);
        transaction.setCategoryCode(categoryCode);
        transaction.setSource(source);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setOriginalTimestamp(originalTimestamp);
        transaction.setProcessedTimestamp(processedTimestamp);
        transaction.setMerchantId(merchantId);
        transaction.setMerchantName(merchantName);
        transaction.setMerchantCity(merchantCity);
        transaction.setMerchantZip(merchantZip);
        transactions.put(transaction.getId(), transaction);
    }

    @Override
    public Transaction findById(String id) {
        return transactions.get(id);
    }

    @Override
    public Page<Transaction> findAll(Pageable pageable) {
        List<Transaction> allTransactions = new ArrayList<>(transactions.values());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allTransactions.size());
        return new PageImpl<>(allTransactions.subList(start, end), pageable, allTransactions.size());
    }

    @Override
    public Page<Transaction> findByTransactionIdContaining(String transactionId, Pageable pageable) {
        List<Transaction> filteredTransactions = transactions.values().stream()
                .filter(t -> t.getId().contains(transactionId))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredTransactions.size());

        return new PageImpl<>(filteredTransactions.subList(start, end), pageable, filteredTransactions.size());
    }

    // New method to find transactions within a date range
    @Override
    public List<Transaction> findByOriginalTimestampBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return transactions.values().stream()
                .filter(t -> !t.getOriginalTimestamp().isBefore(startDate) && !t.getOriginalTimestamp().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null || transaction.getId().isEmpty()) {
            transaction.setId(generateId()); // Simple ID generation
        }
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public void deleteById(String id) {
        transactions.remove(id);
    }

     private String generateId() {
        return String.valueOf(System.currentTimeMillis() + new Random().nextInt(1000)); // More unique ID
    }
}