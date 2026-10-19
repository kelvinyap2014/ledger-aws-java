package fintech.ledger.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsClient;

@TestConfiguration
public class TestAwsConfig {

    @Bean
    @Primary
    public S3Client s3Client() {
        // Return a mock s3Client for tests to avoid AWS errors in CI/CD
        return Mockito.mock(S3Client.class);
    }

    @Bean
    @Primary
    public S3Presigner s3Presigner() {
        // Return a mock S3Presigner for tests to avoid AWS errors in CI/CD
        return Mockito.mock(S3Presigner.class);
    }

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
