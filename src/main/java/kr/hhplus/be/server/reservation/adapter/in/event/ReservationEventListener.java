package kr.hhplus.be.server.reservation.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.PaidReservationFailEvent;
import kr.hhplus.be.server.reservation.port.in.reservation.PaidReservationUseCase;
import kr.hhplus.be.server.user.domain.PaidUserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationEventListener {

	private final PaidReservationUseCase paidReservationUseCase;

	@KafkaListener(topics = "paid-user", groupId = "paid-reservation")
	public void paidUserEventListener(PaidUserEvent event) throws CustomException {
		log.info("유저 결제 성공 이벤트 수신");
		paidReservationUseCase.paidReservation(event);
	}

	@KafkaListener(topics = "paid-reservation-fail", groupId = "paid-reservation-fail")
	public void paidReservationFailEventListener(PaidReservationFailEvent event) throws CustomException {
		log.info("좌석 결제 실패 이벤트 수신");
		paidReservationUseCase.paidReservationFail(event);
	}
}
