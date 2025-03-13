package com.example.carddemo.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    private String id;
    private String cardNumber;
    private String typeCode;
    private String categoryCode;
    private String source;
    private BigDecimal amount;
    private String status;
    private String description;
    private String merchantId;
    private String merchantName;
    private String merchantCity;
    private String merchantZip;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime originalTimestamp;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime processedTimestamp;

    public Transaction(String cardNumber, String typeCode, String status,
                      double amount, String description) {
        this.cardNumber = cardNumber;
        this.typeCode = typeCode;
        this.status = status;
        this.amount = BigDecimal.valueOf(amount);
        this.description = description;
    }
}

