package com.asad.digipay.controller;

import com.asad.digipay.dto.PaymentRequest;
import com.asad.digipay.dto.TransactionResponse;
import com.asad.digipay.entity.Transaction;
import com.asad.digipay.entity.User;
import com.asad.digipay.security.AuthenticatedUserResolver;
import com.asad.digipay.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthenticatedUserResolver userResolver;

    @PostMapping
    public ResponseEntity<?> makePayment(@Valid @RequestBody PaymentRequest request) {
        User user = userResolver.getCurrentUser();
        Transaction tx = paymentService.processPayment(user.getId(), request.getTargetWalletId(), request.getAmount());
        return ResponseEntity.ok(Map.of(
                "message", "Payment successful",
                "transactionId", tx.getId()
        ));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory() {
        User user = userResolver.getCurrentUser();
        List<TransactionResponse> response = paymentService.getHistory(user.getId());
        return ResponseEntity.ok(response);
    }
}
