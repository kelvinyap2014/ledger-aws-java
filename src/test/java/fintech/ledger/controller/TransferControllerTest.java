package fintech.ledger.controller;

import fintech.ledger.service.TransferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @Mock
    private TransferService transferService;

    private TransferController controller;

    @Test
    @DisplayName("Should successfully call executeTransfer with correct parameters")
    void testTransferControllerCallsServiceCorrectly() {
        // Setup
        controller = new TransferController(transferService);
        String expectedTxRef = "TX-12345678";
        
        when(transferService.executeTransfer(
            "test-key-abc",
            "acc-001",
            "acc-002",
            new BigDecimal("250.00"),
            "NZD",
            "Test Transfer"
        )).thenReturn(expectedTxRef);

        // When
        var request = new TransferController.TransferRequest(
            "acc-001",
            "acc-002",
            new BigDecimal("250.00"),
            "NZD",
            "Test Transfer"
        );
        
        var response = controller.executeTransfer("test-key-abc", request);

        // Then
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("SUCCESS", response.getBody().get("status"));
        assertEquals(expectedTxRef, response.getBody().get("transactionReference"));
        assertEquals("test-key-abc", response.getBody().get("idempotencyKey"));
    }
}


