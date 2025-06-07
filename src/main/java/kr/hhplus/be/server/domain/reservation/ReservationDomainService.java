package kr.hhplus.be.server.domain.reservation;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.concert.ConcertDate;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.event.KafkaEventObject;
import kr.hhplus.be.server.domain.event.reservation.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.exception.CustomException;
import kr.hhplus.be.server.usecase.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationDomainService {

	private final EventPublisher eventPublisher;

	public ReservationDomainResult processReservation(ConcertDate concertDate, Seat seat, UUID userId) throws CustomException {
		validateSeatAvailable(seat);

		validateConcertDateDeadline(concertDate);

		Seat reservedSeat = seat.reserve();
		Reservation reservation = Reservation.of(userId, seat.id());
		Payment payment = Payment.of(userId, reservation.id(), seat.price());

		return new ReservationDomainResult(reservedSeat, payment, reservation);
	}

	public void handleReservationSuccess(Reservation reservation, QueueToken queueToken, Payment payment, Seat seat) {
		ReservationCreatedEvent reservationCreatedEvent = ReservationCreatedEvent.of(
			reservation.id(),
			queueToken.userId(),
			payment.id(),
			seat.id(),
			payment.amount(),
			LocalDateTime.now()
		);

		KafkaEventObject<ReservationCreatedEvent> event = KafkaEventObject.from(reservationCreatedEvent);
		eventPublisher.publish(event);
	}

	private void validateSeatAvailable(Seat seat) throws CustomException {
		if (!seat.isAvailable())
			throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);
	}

	private void validateConcertDateDeadline(ConcertDate concertDate) throws CustomException {
		if (!concertDate.checkDeadline())
			throw new CustomException(ErrorCode.OVER_DEADLINE);
	}
}
