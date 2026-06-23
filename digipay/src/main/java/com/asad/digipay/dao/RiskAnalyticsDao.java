package com.asad.digipay.dao;

import com.asad.digipay.dto.RiskFactors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RiskAnalyticsDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_HISTORY_COUNT = "SELECT COUNT(*) FROM transactions WHERE source_wallet_id = ?";
    private static final String SQL_AVG_AMOUNT = "SELECT AVG(amount) FROM transactions WHERE source_wallet_id = ? AND status = 'SUCCESS'";
    private static final String SQL_NEW_RECIPIENT = "SELECT COUNT(*) FROM transactions WHERE source_wallet_id = ? AND target_wallet_id = ? AND status = 'SUCCESS'";
    private static final String SQL_VELOCITY_LAST_HOUR = "SELECT COUNT(*) FROM transactions WHERE source_wallet_id = ? AND timestamp >= ?";

    public RiskFactors getRiskFactors(Long sourceId, Long targetId) {
        Integer historyCount = jdbcTemplate.queryForObject(SQL_HISTORY_COUNT, Integer.class, sourceId);
        int finalHistoryCount = Optional.ofNullable(historyCount).orElse(0);

        BigDecimal averagePast = jdbcTemplate.queryForObject(SQL_AVG_AMOUNT, BigDecimal.class, sourceId);

        Integer recipientCount = jdbcTemplate.queryForObject(SQL_NEW_RECIPIENT, Integer.class, sourceId, targetId);
        boolean isNewRecipient = Optional.ofNullable(recipientCount).orElse(0) == 0;

        Timestamp oneHourAgo = Timestamp.valueOf(LocalDateTime.now().minusHours(1));
        Integer velocityLastHour = jdbcTemplate.queryForObject(SQL_VELOCITY_LAST_HOUR, Integer.class, sourceId, oneHourAgo);
        int finalVelocityLastHour = Optional.ofNullable(velocityLastHour).orElse(0);

        return new RiskFactors(finalHistoryCount, averagePast, isNewRecipient, finalVelocityLastHour);
    }
}
