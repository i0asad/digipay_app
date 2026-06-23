package com.asad.digipay.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
@RequiredArgsConstructor
public class WalletDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_ADD_MONEY = "UPDATE wallets SET balance = balance + ? WHERE id = ?";
    private static final String SQL_DEDUCT_MONEY = "UPDATE wallets SET balance = balance - ? WHERE id = ? AND balance >= ?";

    public int addMoney(Long walletId, BigDecimal amount) {
        return jdbcTemplate.update(SQL_ADD_MONEY, amount, walletId);
    }

    public int deductMoney(Long walletId, BigDecimal amount) {
        return jdbcTemplate.update(SQL_DEDUCT_MONEY, amount, walletId, amount);
    }
}
