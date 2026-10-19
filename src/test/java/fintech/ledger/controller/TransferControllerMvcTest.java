package fintech.ledger.controller;

import fintech.ledger.service.TransferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
class TransferControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferService transferService;

    @Test
    @DisplayName("Should successfully process transfer request via REST API and return transaction reference")
    void transferEndpoint_Success() throws Exception {
        String mockTxRef = "TX-98765432";

        when(transferService.executeTransfer(
                eq("test-key-abc"),
                eq("acc-001"),
                eq("acc-002"),
                any(BigDecimal.class),
                eq("NZD"),
                eq("API Transfer Test")
        )).thenReturn(mockTxRef);

        String requestBody = """
                {
                    "sourceAccountId": "acc-001",
                    "destinationAccountId": "acc-002",
                    "amount": 250.00,
                    "currency": "NZD",
                    "description": "API Transfer Test"
                }
                """;

        mockMvc.perform(post("/api/v1/transfers")
                        .header("Idempotency-Key", "test-key-abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionReference").value(mockTxRef));
    }
}
