package kr.hhplus.be.server.domain.reservation.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.payment.port.in.CreatePaymentInput;
import kr.hhplus.be.server.domain.payment.port.in.ExpirePaymentInput;
import kr.hhplus.be.server.domain.reservation.dto.CreateReservationResult;
import kr.hhplus.be.server.domain.reservation.dto.ExpiredReservationResult;
import kr.hhplus.be.server.domain.reservation.port.in.ReserveSeatCommand;
import kr.hhplus.be.server.domain.reservation.port.out.ReservationRepository;
import kr.hhplus.be.server.domain.seat.port.in.ExpireSeatInput;
import kr.hhplus.be.server.domain.seat.port.in.ReserveSeatInput;
import kr.hhplus.be.server.domain.seatHold.port.in.SeatHoldInput;
import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationService {

	private final ReservationRepository reservationRepository;

	private final SeatHoldInput seatHoldInput;
	private final ReserveSeatInput reserveSeatInput;
	private final CreatePaymentInput createPaymentInput;
	private final ExpireSeatInput expireSeatInput;
	private final ExpirePaymentInput expirePaymentInput;

	@Transactional
	public CreateReservationResult createReservation(ReserveSeatCommand command, QueueToken queueToken) throws CustomException {
		Seat 		savedSeat 		 = reserveSeatInput.reserveSeat(command.seatId());
		Reservation savedReservation = reservationRepository.save(Reservation.of(queueToken.userId(), savedSeat.id()));
		Payment 	savedPayment 	 = createPaymentInput.createPayment(queueToken.userId(), savedReservation.id(), savedSeat.price());

		seatHoldInput.seatHold(savedSeat.id(), queueToken.userId());
		return new CreateReservationResult(savedReservation, savedPayment, savedSeat, queueToken.userId());
	}

	@Transactional
	@DistributedLock(key = "reservation:#reservation.id()")
	public ExpiredReservationResult expireReservation(Reservation reservation) throws CustomException {
		Seat updatedSeat 		= expireSeatInput.expireSeat(reservation.seatId());
		Reservation updatedReservation 	= reservationRepository.save(reservation.expired());
		Payment updatedPayment 		= expirePaymentInput.expirePayment(reservation.id());

		return ExpiredReservationResult.from(updatedReservation.id(), updatedPayment.id(), updatedSeat.id(), updatedReservation.userId());
	}

	public List<Reservation> getPendingReservations() {
		return reservationRepository.findByStatusPending();
	}

	@Transactional
	public Reservation paidReservation(UUID reservationId) throws CustomException {
		Reservation reservation = getReservation(reservationId);
		return reservationRepository.save(reservation.paid());
	}

	public Reservation getReservation(UUID reservationId) throws CustomException {
		return reservationRepository.findById(reservationId)
			.orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
	}
}
