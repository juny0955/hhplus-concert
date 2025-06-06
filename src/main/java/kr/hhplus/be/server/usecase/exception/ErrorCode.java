package kr.hhplus.be.server.usecase.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	// USER
	USER_NOT_FOUND(404, "U001", "해당 사용자를 찾을 수 없습니다."),
	NOT_ENOUGH_MIN_CHARGE_POINT(400, "U002", "최소 충전 금액보다 적은 금액은 충전할 수 없습니다."),

	// CONCERT
	CONCERT_NOT_FOUND(404, "C001", "해당 콘서트를 찾을 수 없습니다"),
	CANNOT_RESERVATION_DATE(400, "C002", "해당 날짜는 예약이 불가능 합니다."),
	CONCERT_DATE_NOT_FOUND(404, "C003", "해당 날짜의 콘서트가 없습니다."),
	OVER_DEADLINE(400, "C004", "해당 날짜의 마감시간이 지났습니다."),
	SEAT_NOT_FOUND(404, "C005", "해당 좌석을 찾을 수 없습니다."),
	ALREADY_RESERVED_SEAT(400, "C006", "해당 좌석은 이미 예약되었습니다."),
	SEAT_LOCK_CONFLICT(409, "C007", "이미 다른 사용자가 예약중입니다."),

	// QUEUE TOKEN
	INVALID_QUEUE_TOKEN(400, "Q001", "대기열 토큰이 유효하지 않습니다."),
	;


	private final int status;
	private final String code;
	private final String message;
}
