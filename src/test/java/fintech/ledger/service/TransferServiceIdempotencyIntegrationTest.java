package fintech.ledger.service;

import fintech.ledger.domain.Account;
import fintech.ledger.repository.AccountRepository;
import fintech.ledger.config.TestAwsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestAwsConfig.class)
@Transactional
class TransferServiceIdempotencyIntegrationTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear Hibernate cache before each test to prevent stale data
        entityManager.clear();
    }

    @Test
    @DisplayName("Should return cached transaction reference and prevent double-charging on idempotent retry")
    void executeTransfer_IdempotencyRetryIntegration() {
        String sourceId = "acc-001-nzd"; // Kelvin Smith: 15000.0000
        String destId = "acc-003-nzd";   // Jane Doe: 250.5000
        BigDecimal transferAmount = new BigDecimal("1000.0000");
        String idempotencyKey = "unique-retry-key-999";

        // 1. First Request (Initial Transfer)
        String firstTxRef = transferService.executeTransfer(
                idempotencyKey,
                sourceId,
                destId,
                transferAmount,
                "NZD",
                "First attempt"
        );

        assertNotNull(firstTxRef);

        // Verify balance after first attempt
        Account sourceAfterFirst = accountRepository.findById(sourceId).orElseThrow();
        assertEquals(new BigDecimal("14000.0000"), sourceAfterFirst.getBalance());

        // 2. Second Request (Retry with the exact same Idempotency-Key)
        String secondTxRef = transferService.executeTransfer(
                idempotencyKey,
                sourceId,
                destId,
                transferAmount,
                "NZD",
                "Retried attempt"
        );

        // 3. Verify that the exact same transaction reference is returned
        assertEquals(firstTxRef, secondTxRef);

        // 4. Verify that balances did NOT change on the retry (no double charging)
        Account sourceAfterRetry = accountRepository.findById(sourceId).orElseThrow();
        assertEquals(new BigDecimal("14000.0000"), sourceAfterRetry.getBalance());
    }
}
