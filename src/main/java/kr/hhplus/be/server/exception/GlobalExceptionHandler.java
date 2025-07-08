package kr.hhplus.be.server.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> customExceptionHandler(CustomException e) {
		return ResponseEntity.status(e.getErrorCode().getStatus())
			.body(ErrorResponse.of(e.getErrorCode()));
	}
}
