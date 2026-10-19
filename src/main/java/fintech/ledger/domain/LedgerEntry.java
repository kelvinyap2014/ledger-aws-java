package fintech.ledger.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerEntry {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(name = "transaction_reference", nullable = false, length = 50)
    private String transactionReference;

    @Column(name = "account_id", nullable = false, length = 36)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 6)
    private EntryType entryType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    public enum EntryType {
        DEBIT, CREDIT
    }

    public static LedgerEntry createDebit(String txRef, String accountId, BigDecimal amount, String currency, String desc) {
        return LedgerEntry.builder()
                .id(UUID.randomUUID().toString())
                .transactionReference(txRef)
                .accountId(accountId)
                .entryType(EntryType.DEBIT)
                .amount(amount)
                .currency(currency)
                .description(desc)
                .createdAt(OffsetDateTime.now())
                .build();
    }

    public static LedgerEntry createCredit(String txRef, String accountId, BigDecimal amount, String currency, String desc) {
        return LedgerEntry.builder()
                .id(UUID.randomUUID().toString())
                .transactionReference(txRef)
                .accountId(accountId)
                .entryType(EntryType.CREDIT)
                .amount(amount)
                .currency(currency)
                .description(desc)
                .createdAt(OffsetDateTime.now())
                .build();
    }
}
