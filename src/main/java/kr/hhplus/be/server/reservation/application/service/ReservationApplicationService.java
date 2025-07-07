package kr.hhplus.be.server.reservation.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.ports.in.concert.ValidOpenConcertInput;
import kr.hhplus.be.server.concert.ports.in.concertDate.ValidDeadLineInput;
import kr.hhplus.be.server.concert.ports.in.seat.ExpireSeatInput;
import kr.hhplus.be.server.concert.ports.in.seat.ReserveSeatInput;
import kr.hhplus.be.server.concert.ports.out.SeatHoldRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.ports.in.CreatePaymentInput;
import kr.hhplus.be.server.payment.ports.in.ExpirePaymentInput;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.ports.in.GetActiveQueueTokenInput;
import kr.hhplus.be.server.reservation.application.dto.CreateReservationResult;
import kr.hhplus.be.server.reservation.application.dto.ExpiredReservationResult;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.ports.in.ReserveSeatCommand;
import kr.hhplus.be.server.reservation.ports.out.ReservationRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationApplicationService {

	private final ReservationRepository reservationRepository;
	private final GetActiveQueueTokenInput getActiveQueueTokenInput;
	private final ValidOpenConcertInput validOpenConcertInput;
	private final ValidDeadLineInput validDeadLineInput;
	private final SeatHoldRepository seatHoldRepository;
	private final ReserveSeatInput reserveSeatInput;
	private final CreatePaymentInput createPaymentInput;
	private final ExpireSeatInput expireSeatInput;
	private final ExpirePaymentInput expirePaymentInput;

	@Transactional
	public CreateReservationResult createReservation(ReserveSeatCommand command) throws CustomException {
		QueueToken queueToken = getActiveQueueTokenInput.getActiveQueueToken(command.queueTokenId());

		validOpenConcertInput.validOpenConcert(command.concertId());
		validDeadLineInput.validDeadLine(command.concertDateId());

		Seat savedSeat = reserveSeatInput.reserveSeat(command.seatId());
		Reservation savedReservation = reservationRepository.save(Reservation.of(queueToken.userId(), savedSeat.id()));
		Payment savedPayment = createPaymentInput.createPayment(queueToken.userId(), savedReservation.id(), savedSeat.price());

		seatHoldRepository.hold(savedSeat.id(), queueToken.userId());
		return new CreateReservationResult(savedReservation, savedPayment, savedSeat, queueToken.userId());
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public ExpiredReservationResult expireReservation(Reservation reservation) throws CustomException {
		if (!seatHoldRepository.isHoldSeat(reservation.seatId(), reservation.userId()))
			return null;

		Seat 		updatedSeat 		= expireSeatInput.expireSeat(reservation.seatId());
		Reservation updatedReservation 	= reservationRepository.save(reservation.expired());
		Payment 	updatedPayment 		= expirePaymentInput.expirePayment(reservation.id());

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
