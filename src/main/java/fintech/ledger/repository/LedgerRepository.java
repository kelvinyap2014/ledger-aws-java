package fintech.ledger.repository;

import fintech.ledger.domain.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntry, String> {
    List<LedgerEntry> findByTransactionReference(String transactionReference);

    List<LedgerEntry> findByAccountId(String accountId);
}
