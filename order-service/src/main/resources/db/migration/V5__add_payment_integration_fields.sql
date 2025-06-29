-- Add payment integration fields to orders table
ALTER TABLE orders 
ADD COLUMN payment_reference VARCHAR(100),
ADD COLUMN payment_gateway ENUM('STRIPE', 'PAYPAL', 'VNPAY', 'MOMO', 'ZALOPAY', 'BANK_TRANSFER', 'CASH_ON_DELIVERY', 'CREDIT_CARD', 'DEBIT_CARD'),
ADD COLUMN payment_gateway_transaction_id VARCHAR(255),
ADD COLUMN payment_initiated_at TIMESTAMP,
ADD COLUMN payment_completed_at TIMESTAMP,
ADD COLUMN payment_failed_at TIMESTAMP;

-- Add indexes for payment fields
CREATE INDEX idx_orders_payment_reference ON orders(payment_reference);
CREATE INDEX idx_orders_payment_gateway ON orders(payment_gateway);
CREATE INDEX idx_orders_payment_gateway_transaction_id ON orders(payment_gateway_transaction_id);
CREATE INDEX idx_orders_payment_initiated_at ON orders(payment_initiated_at);
CREATE INDEX idx_orders_payment_completed_at ON orders(payment_completed_at);
