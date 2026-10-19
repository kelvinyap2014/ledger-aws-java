package fintech.ledger.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.sqs.SqsClient;

@TestConfiguration
public class TestAwsConfig {

    @Bean
    @Primary
    public SqsClient sqsClient() {
        // Return a mock SqsClient for tests to avoid AWS credential errors in CI/CD
        return Mockito.mock(SqsClient.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
