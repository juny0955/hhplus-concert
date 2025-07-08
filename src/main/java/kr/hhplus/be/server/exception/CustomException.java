package kr.hhplus.be.server.exception;

import lombok.Getter;

@Getter
public class CustomException extends Exception {

	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
