package com.asad.digipay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long sourceWalletId,
        Long targetWalletId,
        BigDecimal amount,
        String status,
        LocalDateTime timestamp
) {
}
