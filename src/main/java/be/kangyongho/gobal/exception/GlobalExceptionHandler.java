package be.kangyongho.gobal.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex) {
		log.error("계좌를 찾을 수 없음: {}", ex.getMessage(), ex);
		ErrorResponse errorResponse = new ErrorResponse(
			HttpStatus.NOT_FOUND.value(),
			ex.getMessage(),
			LocalDateTime.now()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException ex) {
		log.error("잔액 부족: {}", ex.getMessage(), ex);
		ErrorResponse errorResponse = new ErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			ex.getMessage(),
			LocalDateTime.now()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		log.error("유효성 검사 오류: {}", ex.getMessage(), ex);
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			String errorMessage = error.getDefaultMessage();
			log.debug("필드 오류: {} - {}", error.getField(), errorMessage);
			errors.put(error.getField(), errorMessage);
		});

		ValidationErrorResponse response = new ValidationErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			"Validation error",
			LocalDateTime.now(),
			errors
		);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		log.error("서버 내부 오류 발생:", ex);

		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		log.error("상세 스택 트레이스: {}", sw);

		ErrorResponse errorResponse = new ErrorResponse(
			HttpStatus.INTERNAL_SERVER_ERROR.value(),
			"서버 내부 오류가 발생했습니다.",
			LocalDateTime.now()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Getter
	@AllArgsConstructor
	public static class ErrorResponse {
		private int status;
		private String message;
		private LocalDateTime timestamp;
	}

	@Getter
	@AllArgsConstructor
	public static class ValidationErrorResponse {
		private int status;
		private String message;
		private LocalDateTime timestamp;
		private Map<String, String> errors;
	}
}