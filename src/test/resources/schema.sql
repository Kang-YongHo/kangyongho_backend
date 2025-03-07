-- 계좌 테이블
CREATE TABLE IF NOT EXISTS account (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       account_number VARCHAR(255) NOT NULL UNIQUE,
    owner_name VARCHAR(255) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 거래 테이블
CREATE TABLE IF NOT EXISTS transaction (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           source_account_id BIGINT,
                                           target_account_id BIGINT,
                                           amount DECIMAL(19, 2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    fee DECIMAL(19, 2),
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );