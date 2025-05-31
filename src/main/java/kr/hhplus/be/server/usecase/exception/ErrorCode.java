package kr.hhplus.be.server.usecase.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	USER_NOT_FOUND(404, "U001", "해당 사용자를 찾을 수 없습니다."),
	NOT_ENOUGH_MIN_CHARGE_POINT(400, "U002", "최소 충전 금액보다 적은 금액은 충전할 수 없습니다.")

	;

	private final int status;
	private final String code;
	private final String message;
}
