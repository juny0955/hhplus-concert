package kr.hhplus.be.server.reservation.port.out;

import kr.hhplus.be.server.reservation.domain.PaidReservationEvent;
import kr.hhplus.be.server.reservation.domain.PaidUserFailEvent;

public interface ReservationEventPublishPort {
	/**
	 * 예역 결제 성공 이벤트 발행
	 * @param event 예약 결제 성공 이벤트
	 */
	void publishPaidReservationEvent(PaidReservationEvent event);

	/**
	 * 포인트 결제 이벤트 처리중 실패 이벤트 발행
	 * @param event 포인트 결제 실패 이벤트
	 */
	void publishPaidUserFailEvent(PaidUserFailEvent event);
}
