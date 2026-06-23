package com.asad.digipay.controller;

import com.asad.digipay.dto.AddMoneyRequest;
import com.asad.digipay.dto.WalletResponse;
import com.asad.digipay.entity.User;
import com.asad.digipay.entity.Wallet;
import com.asad.digipay.security.AuthenticatedUserResolver;
import com.asad.digipay.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final AuthenticatedUserResolver userResolver;

    @GetMapping
    public ResponseEntity<?> getMyWallet() {
        User user = userResolver.getCurrentUser();
        Wallet wallet = walletService.getWalletByUserId(user.getId());
        return ResponseEntity.ok(new WalletResponse(wallet.getId(), wallet.getBalance()));
    }

    @PostMapping("/add-money")
    public ResponseEntity<?> addMoney(@Valid @RequestBody AddMoneyRequest request) {
        User user = userResolver.getCurrentUser();
        boolean success = walletService.addMoney(user.getId(), request.getCardNumber(), request.getCvv(), request.getExpiry(), request.getAmount());
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Money added successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to add money. Please check card details."));
        }
    }
}
