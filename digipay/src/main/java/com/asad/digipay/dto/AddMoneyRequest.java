package com.asad.digipay.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AddMoneyRequest {
    @NotBlank(message = "Card number cannot be blank")
    private String cardNumber;
    
    @NotBlank(message = "CVV cannot be blank")
    private String cvv;
    
    @NotBlank(message = "Expiry cannot be blank")
    private String expiry;
    
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;
}
