package com.asad.digipay.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_CHECK_CONFLICTS = 
            "SELECT username, email, phone_number, pan_card FROM users WHERE username = ? OR email = ? OR phone_number = ? OR pan_card = ? LIMIT 1";

    public Optional<Map<String, Object>> findUserConflicts(String username, String email, String phone, String panCard) {
        List<Map<String, Object>> conflicts = jdbcTemplate.queryForList(SQL_CHECK_CONFLICTS, username, email, phone, panCard);
        return conflicts.isEmpty() ? Optional.empty() : Optional.of(conflicts.get(0));
    }
}
