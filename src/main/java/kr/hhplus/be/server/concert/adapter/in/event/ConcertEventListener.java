package kr.hhplus.be.server.concert.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.port.in.seat.PaidSeatUseCase;
import kr.hhplus.be.server.concert.port.in.soldoutrank.UpdateSoldOutRankUseCase;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;
import kr.hhplus.be.server.reservation.domain.PaidReservationEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConcertEventListener {

	private final PaidSeatUseCase paidSeatUseCase;
	private final UpdateSoldOutRankUseCase updateSoldOutRankUseCase;

	@KafkaListener(topics = "paid-reservation", groupId = "reserve-seat")
	public void paidReservationEventListener(PaidReservationEvent event) throws CustomException {
		paidSeatUseCase.paidSeat(event);
	}

	@KafkaListener(topics = "payment-success", groupId = "update-rank")
	public void paymentSuccessEventListener(PaymentSuccessEvent event) {
		updateSoldOutRankUseCase.updateSoldOutRank(event);
	}
}
