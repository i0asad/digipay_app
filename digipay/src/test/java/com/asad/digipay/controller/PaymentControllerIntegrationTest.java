package com.asad.digipay.controller;

import com.asad.digipay.dto.PaymentRequest;
import com.asad.digipay.entity.User;
import com.asad.digipay.entity.Wallet;
import com.asad.digipay.repository.TransactionRepository;
import com.asad.digipay.repository.UserRepository;
import com.asad.digipay.repository.WalletRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User sourceUser;
    private Wallet sourceWallet;
    private Wallet targetWallet;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
        
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();

        sourceUser = new User();
        sourceUser.setUsername("testuser");
        sourceUser.setPassword(passwordEncoder.encode("password"));
        sourceUser.setEmail("testuser@example.com");
        sourceUser.setPhoneNumber("+919876543210");
        sourceUser.setPanCard("ABCDE1234F");
        sourceUser = userRepository.save(sourceUser);

        sourceWallet = new Wallet();
        sourceWallet.setUser(sourceUser);
        sourceWallet.setBalance(new BigDecimal("1000.00"));
        sourceWallet = walletRepository.save(sourceWallet);

        User targetUser = new User();
        targetUser.setUsername("targetuser");
        targetUser.setPassword(passwordEncoder.encode("password"));
        targetUser.setEmail("targetuser@example.com");
        targetUser.setPhoneNumber("+919876543211");
        targetUser.setPanCard("FGHIJ5678K");
        targetUser = userRepository.save(targetUser);

        targetWallet = new Wallet();
        targetWallet.setUser(targetUser);
        targetWallet.setBalance(new BigDecimal("500.00"));
        targetWallet = walletRepository.save(targetWallet);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testMakePayment_Success() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setTargetWalletId(targetWallet.getId());
        request.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Payment successful"))
                .andExpect(jsonPath("$.transactionId").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testMakePayment_InsufficientBalance() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setTargetWalletId(targetWallet.getId());
        request.setAmount(new BigDecimal("2000.00"));

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient balance"));
    }
}
