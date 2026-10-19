package fintech.ledger.service;

import fintech.ledger.domain.Account;
import fintech.ledger.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransferServiceIntegrationTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear Hibernate cache and reset mock data before each test
        entityManager.clear();
    }

    @Test
    @DisplayName("Should successfully execute transfer using real database and Liquibase mock data")
    void executeTransfer_IntegrationSuccess() {
        // Given pre-loaded mock accounts from Liquibase changelog (001-test-sample-mock-data.sql)
        String sourceId = "acc-001-nzd"; // Kelvin Smith: 15000.0000
        String destId = "acc-003-nzd";   // Jane Doe: 250.5000
        BigDecimal transferAmount = new BigDecimal("500.0000");

        // When
        String txRef = transferService.executeTransfer(
                "test-idempotency-key-01",
                sourceId,
                destId,
                transferAmount,
                "NZD",
                "Integration test payment"
        );

        // Then
        assertNotNull(txRef);
        assertTrue(txRef.startsWith("TX-"));

        Account updatedSource = accountRepository.findById(sourceId).orElseThrow();
        Account updatedDest = accountRepository.findById(destId).orElseThrow();

        assertEquals(new BigDecimal("14500.0000"), updatedSource.getBalance());
        assertEquals(new BigDecimal("750.5000"), updatedDest.getBalance());
    }
}
