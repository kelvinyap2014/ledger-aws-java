package fintech.ledger.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SettlementEvent(
        String transactionReference,
        String sourceAccountId,
        String destinationAccountId,
        BigDecimal amount,
        String currency,
        OffsetDateTime timestamp
) {}
