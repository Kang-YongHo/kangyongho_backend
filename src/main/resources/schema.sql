-- 계좌 테이블
CREATE TABLE IF NOT EXISTS accounts (
                                        id BIGSERIAL PRIMARY KEY,
                                        account_number VARCHAR(20) NOT NULL UNIQUE,
    owner_name VARCHAR(100) NOT NULL,
    balance DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

-- 거래 테이블
CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGSERIAL PRIMARY KEY,
                                            source_account_id BIGINT,
                                            target_account_id BIGINT,
                                            amount DECIMAL(18, 2) NOT NULL,
    fee DECIMAL(18, 2),
    type VARCHAR(20) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (source_account_id) REFERENCES accounts(id),
    FOREIGN KEY (target_account_id) REFERENCES accounts(id)
    );

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_transactions_source_account_id ON transactions(source_account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_target_account_id ON transactions(target_account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_timestamp ON transactions(timestamp);