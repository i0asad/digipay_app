package com.asad.digipay.service;

import com.asad.digipay.entity.Wallet;
import com.asad.digipay.exception.WalletNotFoundException;
import com.asad.digipay.dao.WalletDao;
import com.asad.digipay.repository.WalletRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletDao walletDao;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));
    }

    @Transactional
    public boolean addMoney(Long userId, String cardNumber, String cvv, String expiry, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return false;
        
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) {
            return false;
        }

        if (cvv == null || !cvv.matches("\\d{3,4}")) {
            return false;
        }

        if (expiry == null || !expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            return false;
        }

        if (cardNumber.startsWith("0000")) {
            return false;
        }

        Wallet wallet = getWalletByUserId(userId);
        int rowsUpdated = walletDao.addMoney(wallet.getId(), amount);
        
        entityManager.flush();
        entityManager.clear();
        
        return rowsUpdated > 0;
    }
}
