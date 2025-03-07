# 와이어바알리 백엔드 과제

## 실행법
- 프로젝트 경로로 이동
- ./gradlew clean build
- docker compose up --build

## API 명세서
- http://localhost:8080/swagger-ui/index.html

## 프로젝트 구조
```.
├── java
│   └── be
│       └── kangyongho
│           ├── KangyonghoApplication.java
│           ├── bank
│           │   ├── component
│           │   │   ├── TransferFeeCalculator.java
│           │   │   ├── TransferLimitChecker.java
│           │   │   └── WithdrawalLimitChecker.java
│           │   ├── controller
│           │   │   ├── AccountApiController.java
│           │   │   └── TransactionApiController.java
│           │   ├── dto
│           │   │   ├── request
│           │   │   └── response
│           │   ├── entity
│           │   │   ├── Account.java
│           │   │   └── Transaction.java
│           │   ├── repository
│           │   │   ├── AccountRepository.java
│           │   │   └── TransactionRepository.java
│           │   └── service
│           │       ├── AccountService.java
│           │       └── TransactionService.java
│           ├── config
│           │   └── SwaggerConfig.java
│           └── gobal
│               └── exception
│                   ├── AccountNotFoundException.java
│                   ├── DailyTransferLimitExceededException.java
│                   ├── DailyWithdrawalLimitExceededException.java
│                   ├── GlobalExceptionHandler.java
│                   └── InsufficientBalanceException.java
└── resources
    ├── application-dev.yml
    ├── application.yml
    ├── db
    │   └── migration
    │       └── V1__init.sql
    └── schema.sql
```

