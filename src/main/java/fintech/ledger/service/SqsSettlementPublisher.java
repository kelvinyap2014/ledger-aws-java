package fintech.ledger.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fintech.ledger.domain.SettlementEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsSettlementPublisher {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.settlement-queue-url}")
    private String queueUrl;

    public void publish(SettlementEvent event) {
        log.info("Publishing settlement event to SQS Queue: {}", queueUrl);
        try {
            String messageBody = objectMapper.writeValueAsString(event);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();

            sqsClient.sendMessage(request);
            log.info("Published settlement event to SQS for transaction: {}", event.transactionReference());
        } catch (Exception e) {
            log.error("Failed to publish settlement event to SQS", e);
            // Depending on strictness, you can either throw or log depending on whether
            // downstream settlement failure should block the primary transaction.
        }
    }
}
