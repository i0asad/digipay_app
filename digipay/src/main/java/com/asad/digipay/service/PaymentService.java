package com.asad.digipay.service;

import com.asad.digipay.dto.TransactionResponse;
import com.asad.digipay.entity.Transaction;
import com.asad.digipay.entity.Wallet;
import com.asad.digipay.enums.TransactionStatus;
import com.asad.digipay.exception.InsufficientBalanceException;
import com.asad.digipay.exception.PaymentRejectedException;
import com.asad.digipay.exception.SameWalletTransferException;
import com.asad.digipay.exception.WalletNotFoundException;
import com.asad.digipay.dao.WalletDao;
import com.asad.digipay.repository.TransactionRepository;
import com.asad.digipay.repository.WalletRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final WalletRepository walletRepository;
    private final WalletDao walletDao;
    private final TransactionRepository transactionRepository;

    private final RiskService riskService;
    private final EntityManager entityManager;

    @Transactional
    public Transaction processPayment(Long sourceUserId, Long targetWalletId, BigDecimal amount) {
        Wallet sourceWallet = walletRepository.findByUserId(sourceUserId)
                .orElseThrow(() -> new WalletNotFoundException("Source wallet not found"));

        Wallet targetWallet = walletRepository.findById(targetWalletId)
                .orElseThrow(() -> new WalletNotFoundException("Target wallet not found"));

        if (sourceWallet.getId().equals(targetWallet.getId())) {
            throw new SameWalletTransferException("Cannot transfer to same wallet");
        }

        if (sourceWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        TransactionStatus riskLevel = riskService.assessRisk(sourceWallet.getId(), targetWallet.getId(), amount);

        Transaction transaction = new Transaction();
        transaction.setSourceWallet(sourceWallet);
        transaction.setTargetWallet(targetWallet);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());

        if (riskLevel == TransactionStatus.REJECTED_BY_RISK) {
            transaction.setStatus(TransactionStatus.REJECTED_BY_RISK);
            transactionRepository.save(transaction);
            throw new PaymentRejectedException("Transaction rejected by risk assessment");
        }

        int sourceUpdated = walletDao.deductMoney(sourceWallet.getId(), amount);
                
        if (sourceUpdated == 0) {
            throw new InsufficientBalanceException("Insufficient balance or concurrent update failed");
        }

        walletDao.addMoney(targetWallet.getId(), amount);

        entityManager.flush();
        entityManager.clear();

        transaction.setStatus(TransactionStatus.SUCCESS);
        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getHistory(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
                
        List<Transaction> transactions = transactionRepository.findBySourceWalletIdOrTargetWalletId(wallet.getId(), wallet.getId());

        return transactions.stream()
                .map(tx -> new TransactionResponse(
                        tx.getId(),
                        tx.getSourceWallet() != null ? tx.getSourceWallet().getId() : null,
                        tx.getTargetWallet().getId(),
                        tx.getAmount(),
                        tx.getStatus().name(),
                        tx.getTimestamp()
                ))
                .sorted((a, b) -> Long.compare(b.id(), a.id()))
                .toList();
    }
}
