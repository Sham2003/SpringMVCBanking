package com.banking;

import com.banking.model.User;
import com.banking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        jdbcTemplate.execute("DELETE FROM users where email = 'sham@example.com'");
    }

    @Test
    public void testSaveAndFindByEmail() {
        User user = new User();
        user.setName("Sham");
        user.setEmail("sham@example.com");
        user.setPassword("password123");
        user.setOtp("000000");
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);

        userRepository.save(user);

        User found = userRepository.findByEmail("sham@example.com");

        assertNotNull(found);
        assertEquals("Sham", found.getName());
        assertEquals("sham@example.com", found.getEmail());

        userRepository.delete(found);
        System.out.println("User Repository Tests done successfully");
    }
}

