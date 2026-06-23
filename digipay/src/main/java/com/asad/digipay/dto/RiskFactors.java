package com.asad.digipay.dto;

import java.math.BigDecimal;

public record RiskFactors(
    int historyCount,
    BigDecimal averagePast,
    boolean isNewRecipient,
    int velocityLastHour
) {}
