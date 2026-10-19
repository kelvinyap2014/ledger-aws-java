package fintech.ledger.service;

import fintech.ledger.domain.IdempotencyKey;
import fintech.ledger.domain.LedgerEntry;
import fintech.ledger.domain.SettlementEvent;
import fintech.ledger.repository.AccountRepository;
import fintech.ledger.repository.IdempotencyRepository;
import fintech.ledger.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final LedgerRepository ledgerRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final SqsSettlementPublisher sqsPublisher;

    @Transactional
    public String executeTransfer(String idempotencyKey, String sourceAccountId, String destinationAccountId, BigDecimal amount, String currency, String description) {

        log.info("Begin transfer from {} to {} of amount {} {}", sourceAccountId, destinationAccountId, amount, currency);
        // 1. Enforce Idempotency if key is provided
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existingKeyOpt = idempotencyRepository.findById(idempotencyKey);
            if (existingKeyOpt.isPresent()) {
                // Request has already been processed! Return the stored transaction reference.
                return existingKeyOpt.get().getResponseBody();
            }
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be strictly positive");
        }

        // 2. Lock accounts using pessimistic write lock
        var sourceAccount = accountRepository.findByIdForUpdate(sourceAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found: " + sourceAccountId));
        var destAccount = accountRepository.findByIdForUpdate(destinationAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found: " + destinationAccountId));

        // 3. Validate sufficient funds
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds in source account");
        }

        // 4. Generate unique transaction reference
        String txRef = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 5. Create balanced double-entry ledger legs
        var debitEntry = LedgerEntry.createDebit(txRef, sourceAccountId, amount, currency, description);
        var creditEntry = LedgerEntry.createCredit(txRef, destinationAccountId, amount, currency, description);

        ledgerRepository.saveAll(List.of(debitEntry, creditEntry));

        // 6. Update balances atomically
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destAccount.setBalance(destAccount.getBalance().add(amount));
        var now = OffsetDateTime.now();
        sourceAccount.setUpdatedAt(now);
        destAccount.setUpdatedAt(now);
        accountRepository.saveAll(List.of(sourceAccount, destAccount));

        // 7. Save Idempotency Key to prevent duplicate processing on retry
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            IdempotencyKey keyRecord = IdempotencyKey.builder()
                    .idempotencyKey(idempotencyKey)
                    .requestHash(sourceAccountId + destinationAccountId + amount.toPlainString()) // simple verification hash
                    .responseBody(txRef) // Storing transaction reference as cached response
                    .createdAt(OffsetDateTime.now())
                    .build();
            idempotencyRepository.save(keyRecord);
        }

        // 1. Emit asynchronous settlement event to SQS (Non-blocking)
        SettlementEvent event = new SettlementEvent(
                txRef,
                sourceAccountId,
                destinationAccountId,
                amount,
                currency,
                OffsetDateTime.now()
        );
        sqsPublisher.publish(event);

        log.info("End transfer from {} to {} of amount {} {}, transaction reference: {}",
                sourceAccountId, destinationAccountId, amount, currency, txRef);

        return txRef;
    }
}
