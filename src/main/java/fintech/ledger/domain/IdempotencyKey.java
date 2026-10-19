package fintech.ledger.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "idempotency_keys")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyKey {

    @Id
    @Column(name = "idempotency_key", length = 100, nullable = false)
    private String idempotencyKey;

    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;

    @Lob
    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
