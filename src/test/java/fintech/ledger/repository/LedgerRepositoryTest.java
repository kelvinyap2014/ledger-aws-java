package fintech.ledger.repository;

import fintech.ledger.domain.LedgerEntry;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LedgerRepositoryTest {

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear Hibernate cache before each test
        entityManager.clear();
    }

    @Test
    @DisplayName("Should successfully save and retrieve double-entry ledger entries for a transaction reference")
    void saveAndFindByTransactionReference() {
        String txRef = "TX-TEST-12345";

        LedgerEntry debitEntry = LedgerEntry.createDebit(
                txRef,
                "acc-001-nzd",
                new BigDecimal("100.0000"),
                "NZD",
                "Test Debit"
        );

        LedgerEntry creditEntry = LedgerEntry.createCredit(
                txRef,
                "acc-003-nzd",
                new BigDecimal("100.0000"),
                "NZD",
                "Test Credit"
        );

        ledgerRepository.saveAll(List.of(debitEntry, creditEntry));

        List<LedgerEntry> entries = ledgerRepository.findByTransactionReference(txRef);

        assertEquals(2, entries.size());

        boolean hasDebit = entries.stream().anyMatch(e -> e.getEntryType() == LedgerEntry.EntryType.DEBIT);
        boolean hasCredit = entries.stream().anyMatch(e -> e.getEntryType() == LedgerEntry.EntryType.CREDIT);

        assertTrue(hasDebit, "Should contain a DEBIT entry");
        assertTrue(hasCredit, "Should contain a CREDIT entry");
    }
}
