package kr.hhplus.be.server.reservation.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.reservation.port.in.reservation.PaidReservationUseCase;
import kr.hhplus.be.server.user.domain.PaidUserEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

	private final PaidReservationUseCase paidReservationUseCase;

	@KafkaListener(topics = "paid-user", groupId = "reservation")
	public void paidUserEventListener(PaidUserEvent event) throws CustomException {
		paidReservationUseCase.paidReservation(event);
	}
}
