package kr.hhplus.be.server.interfaces.api.advice;

import kr.hhplus.be.server.usecase.exception.ErrorCode;

public record ErrorResponse(
	String code,
	String message
) {
	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
	}
}
