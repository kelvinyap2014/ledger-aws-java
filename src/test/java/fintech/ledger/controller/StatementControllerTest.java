package fintech.ledger.controller;

import fintech.ledger.service.StatementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatementController.class)
class StatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatementService statementService;

    @Test
    @DisplayName("Should successfully generate and return a 15-minute pre-signed S3 statement URL for an account")
    void getStatementUrl_Success() throws Exception {
        String accountId = "acc-001";
        String mockPresignedUrl = "https://ledger-audit-statements-dev.s3.ap-southeast-2.amazonaws.com/statements/acc-001/statement-2026-10-20.csv?AWSAccessKeyId=MOCK&Signature=XYZ&Expires=1792670000";

        when(statementService.generateOrGetStatementPresignedUrl(accountId))
                .thenReturn(mockPresignedUrl);

        mockMvc.perform(get("/api/v1/accounts/{id}/statement/url", accountId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.statementUrl").value(mockPresignedUrl))
                .andExpect(jsonPath("$.expiresInMinutes").value("15"));
    }
}
