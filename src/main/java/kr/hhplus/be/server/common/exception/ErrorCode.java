package kr.hhplus.be.server.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	// USER
	USER_NOT_FOUND(404, "U001", "해당 사용자를 찾을 수 없습니다."),
	NOT_ENOUGH_MIN_CHARGE_POINT(400, "U002", "최소 충전 금액보다 적은 금액은 충전할 수 없습니다."),
	INSUFFICIENT_BALANCE(400, "U003", "보유 포인트가 부족합니다."),

	// CONCERT
	CONCERT_NOT_FOUND(404, "C001", "해당 콘서트를 찾을 수 없습니다"),
	CANNOT_RESERVATION_DATE(400, "C002", "해당 날짜는 예약이 불가능 합니다."),
	CONCERT_DATE_NOT_FOUND(404, "C003", "해당 날짜의 콘서트가 없습니다."),
	OVER_DEADLINE(400, "C004", "해당 날짜의 마감시간이 지났습니다."),
	SEAT_NOT_FOUND(404, "C005", "해당 좌석을 찾을 수 없습니다."),
	CONCERT_NOT_OPEN(400, "C006", "티켓팅 시작 전 입니다."),

	// RESERVATION
	RESERVATION_NOT_FOUND(404, "R001", "해당 예약정보를 찾을 수 없습니다."),
	RESERVATION_EXPIRED(400, "R002", "만료된 예약입니다."),
	RESERVATION_STATUS_NOT_PENDING(400, "R003", "예약 상태가 대기중이 아닙니다"),

	// PAYMENT
	PAYMENT_NOT_FOUND(404, "P001", "해당 결제정보를 찾을 수 없습니다."),
	ALREADY_PAID(400, "P002", "이미 결제되었습니다."),
	INVALID_PAYMENT_AMOUNT(400, "P003", "결제 금액이 잘못되었습니다."),
	PAYMENT_STATUS_NOT_PENDING(400, "P004", "결제 상태가 대기중이 아닙니다."),

	// SEAT
	SEAT_NOT_HOLD(409, "S002", "해당 좌석은 임시 배정되어있지 않습니다."),
	SEAT_STATUS_NOT_RESERVED(400, "S003", "좌석 상태가 예약중이 아닙니다."),
	ALREADY_RESERVED_SEAT(400, "S004", "해당 좌석은 이미 예약되었습니다."),
	ALREADY_AVAILABLE_SEAT(400, "S005", "해당 좌석은 이미 예약가능 상태입니다."),

	// QUEUE TOKEN
	INVALID_QUEUE_TOKEN(400, "Q001", "대기열 토큰이 유효하지 않습니다."),

	// LOCK
	LOCK_CONFLICT(409, "L001", "다시 시도해주세요."),

	// SERVER
	INTERNAL_SERVER_ERROR(500, "S001", "시스템 에러 발생"),
	;

	private final int status;
	private final String code;
	private final String message;
}
