package com.asad.digipay.service;

import com.asad.digipay.dto.RiskFactors;
import com.asad.digipay.enums.TransactionStatus;
import com.asad.digipay.dao.RiskAnalyticsDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskService {

    private final RiskAnalyticsDao riskAnalyticsDao;

    public TransactionStatus assessRisk(Long sourceId, Long targetId, BigDecimal amount) {
        RiskFactors factors = riskAnalyticsDao.getRiskFactors(sourceId, targetId);

        double currentAmountRatio = (factors.averagePast() != null && factors.averagePast().compareTo(BigDecimal.ZERO) > 0) 
                ? amount.divide(factors.averagePast(), 4, RoundingMode.HALF_UP).doubleValue() 
                : 1.0;

        int currentHour = LocalDateTime.now().getHour();
        double riskScore = 0.0;

        // 1. Amount Anomaly: Compare current transaction amount to historical average
        // Ratios > 2.5x are highly suspicious (large unusual transfer).
        if (currentAmountRatio > 2.5) {
            riskScore += 0.3;
        } else if (currentAmountRatio > 1.5) {
            riskScore += 0.1;
        }

        // 2. Velocity Check: High frequency of transactions in a short window
        // >4 transactions in the last hour suggests automated bot behavior or frantic spending.
        if (factors.velocityLastHour() >= 4) {
            riskScore += 0.3;
        } else if (factors.velocityLastHour() >= 2) {
            riskScore += 0.1;
        }

        // 3. New Recipient: Transferring to a previously unseen wallet carries inherent risk.
        if (factors.isNewRecipient()) {
            riskScore += 0.2;
        }

        // 4. Time of Day: Late night transactions (10 PM to 5 AM) statistically correlate with higher fraud.
        if (currentHour < 5 || currentHour > 22) {
            riskScore += 0.2;
        }

        // Cold start
        if (factors.historyCount() < 5) {
            riskScore = Math.min(riskScore, 0.1);
        }

        if (riskScore >= 0.7) {
            return TransactionStatus.REJECTED_BY_RISK;
        }
        return TransactionStatus.SUCCESS;
    }
}
