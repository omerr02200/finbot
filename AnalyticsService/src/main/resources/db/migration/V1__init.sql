CREATE TABLE IF NOT EXISTS spending_summaries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    category VARCHAR(100) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    transaction_count INT NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)