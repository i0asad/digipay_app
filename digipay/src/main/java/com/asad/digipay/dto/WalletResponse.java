package com.asad.digipay.dto;

import java.math.BigDecimal;

public record WalletResponse(Long walletId, BigDecimal balance) {
}
