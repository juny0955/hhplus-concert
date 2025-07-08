package kr.hhplus.be.server.common.framework.exception;

public record ErrorResponse(
	String code,
	String message
) {
	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
	}
}
