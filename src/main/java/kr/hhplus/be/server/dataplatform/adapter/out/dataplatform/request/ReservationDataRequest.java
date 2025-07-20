package kr.hhplus.be.server.dataplatform.adapter.out.dataplatform.request;

import java.util.UUID;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;
import lombok.Builder;

@Builder
public record ReservationDataRequest (
	UUID reservationId,
	UUID paymentId,
	UUID concertId,
	UUID seatId
) {
	public static ReservationDataRequest of(PaymentSuccessEvent event, Concert concert) {
		return ReservationDataRequest.builder()
			.reservationId(event.reservationId())
			.paymentId(event.paymentId())
			.concertId(concert.id())
			.seatId(event.seatId())
			.build();
	}
}
