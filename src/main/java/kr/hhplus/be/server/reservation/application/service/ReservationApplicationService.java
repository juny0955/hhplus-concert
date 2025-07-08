package kr.hhplus.be.server.reservation.application.service;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.ports.in.seat.ExpireSeatInput;
import kr.hhplus.be.server.concert.ports.in.seat.ReserveSeatInput;
import kr.hhplus.be.server.concert.ports.in.seatHold.SeatHoldInput;
import kr.hhplus.be.server.common.framework.exception.CustomException;
import kr.hhplus.be.server.common.framework.exception.ErrorCode;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.ports.in.CreatePaymentInput;
import kr.hhplus.be.server.payment.ports.in.ExpirePaymentInput;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.reservation.application.dto.CreateReservationResult;
import kr.hhplus.be.server.reservation.application.dto.ExpiredReservationResult;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.ports.in.ReserveSeatCommand;
import kr.hhplus.be.server.reservation.ports.out.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationApplicationService {

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
	public ExpiredReservationResult expireReservation(Reservation reservation) throws CustomException {
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
