package fintech.ledger.repository;

import fintech.ledger.domain.IdempotencyKey;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class IdempotencyRepositoryTest {

    @Autowired
    private IdempotencyRepository idempotencyRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear Hibernate cache before each test
        entityManager.clear();
    }

    @Test
    @DisplayName("Should successfully save and retrieve an idempotency record")
    void saveAndFindIdempotencyKey() {
        String key = "test-idempotency-uuid-001";
        String responseRef = "TX-MOCK-REF-999";

        IdempotencyKey idempotencyKey = IdempotencyKey.builder()
                .idempotencyKey(key)
                .responseBody(responseRef)
                .createdAt(OffsetDateTime.now())
                .build();

        idempotencyRepository.save(idempotencyKey);

        Optional<IdempotencyKey> found = idempotencyRepository.findById(key);

        assertTrue(found.isPresent());
        assertEquals(key, found.get().getIdempotencyKey());
        assertEquals(responseRef, found.get().getResponseBody());
    }
}
