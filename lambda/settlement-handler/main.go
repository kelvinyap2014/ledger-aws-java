package main

import (
	"context"
	"encoding/json"
	"log"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
)

// TransactionEvent defines the structure of the incoming ledger event
type TransactionEvent struct {
	TransactionReference string  `json:"transactionReference"`
	SourceAccountId      string  `json:"sourceAccountId"`
	DestinationAccountId string  `json:"destinationAccountId"`
	Amount               float64 `json:"amount"`
	Currency             string  `json:"currency"`
	Status               string  `json:"status"`
}

// HandleRequest processes the SQS batch events
func HandleRequest(ctx context.Context, sqsEvent events.SQSEvent) error {
	for _, record := range sqsEvent.Records {
		log.Printf("Received message from SQS: %s", record.MessageId)

		var event TransactionEvent
		if err := json.Unmarshal([]byte(record.Body), &event); err != nil {
			log.Printf("Failed to unmarshal message body: %v", err)
			continue
		}

		log.Printf("Processing settlement for transaction reference: %s | Amount: %.4f %s",
			event.TransactionReference, event.Amount, event.Currency)
	}

	return nil
}

func main() {
	lambda.Start(HandleRequest)
}
