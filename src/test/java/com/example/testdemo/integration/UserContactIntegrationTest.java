package com.example.testdemo.integration;

import com.example.testdemo.config.JwtService;
import com.example.testdemo.entity.EmailData;
import com.example.testdemo.entity.PhoneData;
import com.example.testdemo.entity.User;
import com.example.testdemo.repository.EmailDataRepository;
import com.example.testdemo.repository.PhoneDataRepository;
import com.example.testdemo.repository.UserRepository;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
@Transactional
public class UserContactIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailDataRepository emailDataRepository;

    @Autowired
    private PhoneDataRepository phoneDataRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String authToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        emailDataRepository.deleteAll();
        phoneDataRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .name("testuser")
                .password(passwordEncoder.encode("password"))
                .emails(new ArrayList<>())
                .build();
        EmailData email = EmailData.builder()
                .email("test@example.com")
                .user(testUser)
                .build();

        testUser.getEmails().add(email);
        testUser = userRepository.save(testUser);

        authToken = "Bearer " + jwtService.generateToken(testUser);

    }

    @Test
    void getUserContacts_ReturnsContacts() throws Exception {
        // Arrange
        EmailData email = EmailData.builder()
                .email("test@example.com")
                .user(testUser)
                .build();

        PhoneData phone = PhoneData.builder()
                .phone("+1234567890")
                .user(testUser)
                .build();

        emailDataRepository.save(email);
        phoneDataRepository.save(phone);

        // Act & Assert
        mockMvc.perform(get("/api/users/{userId}/contacts", testUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emails", hasItem("test@example.com")))
                .andExpect(jsonPath("$.phones", hasItem("+1234567890")));
    }

    @Test
    void addEmail_ValidRequest_CreatesEmail() throws Exception {
        // Arrange
        String requestBody = "{\"email\": \"new@example.com\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/{userId}/contacts/emails", testUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        // Verify in database
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(updatedUser.getEmails().stream()
                .anyMatch(e -> e.getEmail().equals("new@example.com")));
    }

    @Test
    void addEmail_DuplicateEmail_ReturnsConflict() throws Exception {
        // Arrange
        User otherUser = User.builder()
                .name("otheruser")
                .password(passwordEncoder.encode("password"))
                .build();
        userRepository.save(otherUser);

        EmailData existingEmail = EmailData.builder()
                .email("existing@example.com")
                .user(otherUser)
                .build();
        emailDataRepository.save(existingEmail);

        String requestBody = "{\"email\": \"existing@example.com\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/{userId}/contacts/emails", testUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }
}