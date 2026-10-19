package fintech.ledger.repository;

import fintech.ledger.domain.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear Hibernate cache before each test
        entityManager.clear();
    }

    @Test
    @DisplayName("Should find account with pessimistic lock (findByIdForUpdate) using Liquibase mock data")
    void findByIdForUpdate_Success() {
        String accountId = "acc-001-nzd";

        Optional<Account> accountOpt = accountRepository.findByIdForUpdate(accountId);

        assertTrue(accountOpt.isPresent());
        Account account = accountOpt.get();
        assertEquals(accountId, account.getId());
        assertEquals(new BigDecimal("15000.0000"), account.getBalance());
    }
}
