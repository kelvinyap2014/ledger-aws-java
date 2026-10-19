package fintech.ledger.service;

import fintech.ledger.domain.LedgerEntry;
import fintech.ledger.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatementService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final LedgerRepository ledgerRepository;

    @Value("${aws.s3.audit-bucket-name}")
    private String auditBucketName;

    public String generateOrGetStatementPresignedUrl(String accountId) {
        String objectKey = String.format("statements/%s/statement-%s.csv", accountId, OffsetDateTime.now().toLocalDate());

        // 1. Fetch transaction history from PostgreSQL for the account
        List<LedgerEntry> entries = ledgerRepository.findByAccountId(accountId);

        // 2. Generate statement content dynamically (CSV format)
        String csvContent = buildCsvStatement(accountId, entries);

        // 3. Upload generated statement directly to S3
        log.info("Uploading generated statement for account {} to S3 bucket {}", accountId, auditBucketName);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(auditBucketName)
                .key(objectKey)
                .contentType("text/csv")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(csvContent, StandardCharsets.UTF_8));

        // 4. Generate 15-minute Presigned Download URL
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(auditBucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    private String buildCsvStatement(String accountId, List<LedgerEntry> entries) {
        StringBuilder sb = new StringBuilder();
        sb.append("Account ID,Entry ID,Type,Amount,Currency,Timestamp\n");

        if (entries == null || entries.isEmpty()) {
            sb.append(String.format("%s,N/A,NO_TRANSACTIONS,0.00,NZD,%s\n", accountId, OffsetDateTime.now()));
        } else {
            for (LedgerEntry entry : entries) {
                sb.append(String.format("%s,%s,%s,%s,%s,%s\n",
                        entry.getAccountId(),
                        entry.getId(),
                        entry.getEntryType(),
                        entry.getAmount(),
                        entry.getCurrency(),
                        entry.getCreatedAt()));
            }
        }
        return sb.toString();
    }
}
