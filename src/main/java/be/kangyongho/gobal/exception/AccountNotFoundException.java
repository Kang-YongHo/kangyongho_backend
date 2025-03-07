package be.kangyongho.gobal.exception;

public class AccountNotFoundException extends RuntimeException {

	public AccountNotFoundException(String message) {
		super(message);
	}

	public AccountNotFoundException(Long id) {
		super("계좌를 찾을 수 없습니다. ID: " + id);
	}

	public AccountNotFoundException(String accountNumber, String fieldName) {
		super("계좌번호: " + accountNumber + "에 해당하는 계좌를 찾을 수 없습니다.");
	}
}