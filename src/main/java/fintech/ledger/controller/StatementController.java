package fintech.ledger.controller;

import fintech.ledger.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class StatementController {

    private final StatementService statementService;

    @GetMapping("/{id}/statement/url")
    public ResponseEntity<Map<String, String>> getStatementUrl(@PathVariable("id") String accountId) {
        String presignedUrl = statementService.generateOrGetStatementPresignedUrl(accountId);

        return ResponseEntity.ok(Map.of(
                "accountId", accountId,
                "statementUrl", presignedUrl,
                "expiresInMinutes", "15"
        ));
    }
}
