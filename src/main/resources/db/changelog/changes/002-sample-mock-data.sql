-- Liquibase formatted sql
-- changeset ledgerstream:002-sample-mock-data

INSERT INTO accounts (id, account_number, holder_name, currency, balance, status) VALUES
    ('acc-001-nzd', 'NZ01-8839-0012938-00', 'Kelvin Smith', 'NZD', 15000.0000, 'ACTIVE'),
    ('acc-002-nzd', 'NZ01-8839-0098412-00', 'Treasury Ops', 'NZD', 500000.0000, 'ACTIVE'),
    ('acc-003-nzd', 'NZ01-8839-0044123-00', 'Jane Doe', 'NZD', 250.5000, 'ACTIVE');

INSERT INTO ledger_entries (id, transaction_reference, account_id, entry_type, amount, currency, description) VALUES
    ('tx-init-001', 'TX-INIT-20261018-01', 'acc-002-nzd', 'DEBIT', 15000.0000, 'NZD', 'Initial funding for Kelvin Smith'),
    ('tx-init-002', 'TX-INIT-20261018-01', 'acc-001-nzd', 'CREDIT', 15000.0000, 'NZD', 'Initial account balance creation');
