package com.EGM.LMS.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * One-time fix: ensures user_sessions.token and refresh_token columns can store JWTs.
 * Existing DBs may have been created with VARCHAR(255); this alters them to TEXT.
 */
@Component
@Order(Integer.MAX_VALUE)
public class UserSessionTokenColumnFix implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public UserSessionTokenColumnFix(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute(
                "ALTER TABLE user_sessions MODIFY COLUMN token TEXT, MODIFY COLUMN refresh_token TEXT"
            );
        } catch (Exception e) {
            // Ignore: column may already be TEXT, or not MySQL, or table missing
        }
    }
}
