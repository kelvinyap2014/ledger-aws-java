package fintech.ledger.controller;

import fintech.ledger.service.TransferService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> executeTransfer(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody TransferRequest request) {

        // Pass the idempotencyKey to the service layer
        String txRef = transferService.executeTransfer(
                idempotencyKey,
                request.sourceAccountId(),
                request.destinationAccountId(),
                request.amount(),
                request.currency(),
                request.description()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "SUCCESS",
                "transactionReference", txRef,
                "idempotencyKey", idempotencyKey != null ? idempotencyKey : "NONE"
        ));
    }

    public record TransferRequest(
            @NotBlank String sourceAccountId,
            @NotBlank String destinationAccountId,
            @NotNull @DecimalMin(value = "0.0001", inclusive = true) BigDecimal amount,
            @NotBlank String currency,
            String description
    ) {}
}
