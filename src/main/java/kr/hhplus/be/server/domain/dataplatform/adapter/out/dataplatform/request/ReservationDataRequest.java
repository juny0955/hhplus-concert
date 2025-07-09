package kr.hhplus.be.server.domain.dataplatform.adapter.out.dataplatform.request;

import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.payment.domain.PaymentSuccessEvent;
import lombok.Builder;

@Builder
public record ReservationDataRequest (
	UUID reservationId,
	UUID paymentId,
	UUID concertId,
	UUID userId,
	UUID seatId,
	LocalDateTime paidAt
) {
	public static ReservationDataRequest of(PaymentSuccessEvent event, Concert concert) {
		return ReservationDataRequest.builder()
			.reservationId(event.reservation().id())
			.paymentId(event.payment().id())
			.concertId(concert.id())
			.userId(event.user().id())
			.seatId(event.seat().id())
			.paidAt(event.payment().updatedAt())
			.build();
	}
}
