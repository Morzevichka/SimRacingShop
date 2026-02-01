package com.morzevichka.auth_service.repository;

import com.morzevichka.auth_service.model.Role;
import com.morzevichka.auth_service.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
public class UserRepositoryTest {

    @Container
    private final static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private User testUser;

    @BeforeEach
    void setUp() {
        this.testUser = User.builder()
                .email("test@gmail.com")
                .login("test")
                .passwordHash("hashed")
                .role(Role.ROLE_USER)
                .build();
    }


    @Test
    void shouldFindUserByEmail() {
        userRepository.save(testUser);

        Optional<User> foundUser = userRepository.findByEmail("test@gmail.com");

        assertThat(foundUser).isPresent();
    }

    @Test
    void shouldFindUserById() {
        User savedUser = userRepository.save(testUser);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        User savedUser = userRepository.save(testUser);

        boolean exists = userRepository.existsByEmail(savedUser.getEmail());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnTrueWhenLoginExists() {
        User savedUser = userRepository.save(testUser);

        boolean exists = userRepository.existsByLogin(savedUser.getLogin());

        assertThat(exists).isTrue();
    }
}