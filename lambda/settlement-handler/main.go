package main

import (
	"context"
	"encoding/json"
	"log"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
)

// SettlementEvent matches the payload structure sent by Spring Boot
type SettlementEvent struct {
	TransactionReference string  `json:"transactionReference"`
	SourceAccountId      string  `json:"sourceAccountId"`
	DestinationAccountId string  `json:"destinationAccountId"`
	Amount               float64 `json:"amount"`
	Currency             string  `json:"currency"`
	Timestamp            string  `json:"timestamp"`
}

// HandleRequest processes incoming SQS batch events
func HandleRequest(ctx context.Context, sqsEvent events.SQSEvent) error {
	for _, message := range sqsEvent.Records {
		log.Printf("Processing SQS Message ID: %s", message.MessageId)

		// 1. Parse the SQS record payload
		var event SettlementEvent
		err := json.Unmarshal([]byte(message.Body), &event)
		if err != nil {
			log.Printf("ERROR: Failed to unmarshal SQS message body: %v. Body: %s", err, message.Body)
			// Return error so SQS retries processing or routes to DLQ if max retries exceeded
			return err
		}

		log.Printf("Successfully parsed transaction: %s | Amount: %.2f %s | From: %s -> To: %s",
			event.TransactionReference,
			event.Amount,
			event.Currency,
			event.SourceAccountId,
			event.DestinationAccountId,
		)

		// DUMMY: Perform downstream settlement operations
		// -> e.g., logging final clearing status, calling external payment provider APIs,
		//    or updating a settlement audit table in PostgreSQL.
		log.Println("[DUMMY] Executing downstream settlement operations: calling payment provider and updating audit table...")

		// DUMMY: Schedule a lightweight recurring job (or secondary Lambda cron job)
		// -> e.g., queries your PostgreSQL database to verify that the sum of all debits
		//    equals the sum of all credits for every transaction reference, ensuring
		//    mathematical invariant checks hold true.
		log.Println("[DUMMY] Running reconciliation invariant check: validating sum(debits) == sum(credits)...")
	}

	// Returning nil tells SQS that the batch was processed successfully
	// and safely deletes the messages from the queue.
	return nil
}

func main() {
	lambda.Start(HandleRequest)
}
