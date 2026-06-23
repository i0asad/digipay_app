package com.asad.digipay.service;

import com.asad.digipay.dto.AuthRequest;
import com.asad.digipay.dto.RegisterRequest;
import com.asad.digipay.entity.User;
import com.asad.digipay.entity.Wallet;
import com.asad.digipay.dao.UserDao;

import com.asad.digipay.repository.UserRepository;
import com.asad.digipay.repository.WalletRepository;
import com.asad.digipay.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDao userDao;

    @Transactional
    public String registerUser(RegisterRequest request) {
        Optional<Map<String, Object>> conflicts = userDao.findUserConflicts(
                request.getUsername(), request.getEmail(), request.getPhoneNumber(), request.getPanCard()
        );

        if (conflicts.isPresent()) {
            Map<String, Object> row = conflicts.get();
            if (request.getUsername().equals(row.get("username"))) {
                throw new IllegalArgumentException("Username is already taken");
            }
            if (request.getEmail().equals(row.get("email"))) {
                throw new IllegalArgumentException("Email is already registered");
            }
            if (request.getPhoneNumber().equals(row.get("phone_number"))) {
                throw new IllegalArgumentException("Phone number is already registered");
            }
            if (request.getPanCard().equals(row.get("pan_card"))) {
                throw new IllegalArgumentException("PAN card is already registered");
            }
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPanCard(request.getPanCard());
        user = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        walletRepository.save(wallet);

        return "User registered successfully with a wallet containing 100 rupees.";
    }

    public String authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        return jwtUtil.generateToken(authentication.getName());
    }
}
