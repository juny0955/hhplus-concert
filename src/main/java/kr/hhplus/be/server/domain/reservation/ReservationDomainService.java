package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationDomainService {

	public ReservationDomainResult processReservation(Concert concert, ConcertDate concertDate, Seat seat, UUID userId) throws CustomException {
		validateConcertOpenTimeAndDeadline(concert);
		validateSeatAvailable(seat);
		validateConcertDateDeadline(concertDate);

		Seat 		reservedSeat 	= 	seat.reserve();
		Reservation reservation 	= 	Reservation.of(userId, seat.id());

		return new ReservationDomainResult(reservedSeat, reservation, null);
	}

	public ReservationDomainResult processReservationExpired(Reservation reservation, Payment payment, Seat seat) throws CustomException {
		validateExpiredStatus(reservation, payment, seat);

		Seat 		expiredSeat			= seat.expired();
		Reservation expiredReservation 	= reservation.expired();
		Payment 	expiredPayment 		= payment.expired();

		return new ReservationDomainResult(expiredSeat, expiredReservation, expiredPayment);
	}

	private void validateExpiredStatus(Reservation reservation, Payment payment, Seat seat) throws CustomException {
		if (!reservation.isPending())
			throw new CustomException(ErrorCode.RESERVATION_STATUS_NOT_PENDING);
		if (!payment.isPending())
			throw new CustomException(ErrorCode.PAYMENT_STATUS_NOT_PENDING);
		if (!seat.isReserved())
			throw new CustomException(ErrorCode.SEAT_STATUS_NOT_RESERVED);
	}

	private void validateSeatAvailable(Seat seat) throws CustomException {
		if (!seat.isAvailable())
			throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);
	}

	private void validateConcertDateDeadline(ConcertDate concertDate) throws CustomException {
		if (!concertDate.checkDeadline())
			throw new CustomException(ErrorCode.OVER_DEADLINE);
	}

	private void validateConcertOpenTimeAndDeadline(Concert concert) throws CustomException {
		if (!concert.isOpen())
			throw new CustomException(ErrorCode.CONCERT_NOT_OPEN);
	}
}
