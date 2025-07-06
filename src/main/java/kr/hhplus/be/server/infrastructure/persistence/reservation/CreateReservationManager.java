package kr.hhplus.be.server.infrastructure.persistence.reservation;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.concert.domain.concert.ConcertRepository;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDateRepository;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentRepository;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.domain.QueueTokenRepository;
import kr.hhplus.be.server.queue.domain.QueueTokenUtil;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.domain.ReservationDomainResult;
import kr.hhplus.be.server.reservation.domain.ReservationDomainService;
import kr.hhplus.be.server.reservation.domain.ReservationRepository;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.SeatHoldRepository;
import kr.hhplus.be.server.concert.domain.seat.SeatRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.reservation.usecase.input.ReserveSeatCommand;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateReservationManager {

	private final ReservationRepository reservationRepository;
	private final QueueTokenRepository queueTokenRepository;
	private final ConcertRepository concertRepository;
	private final ConcertDateRepository concertDateRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final SeatRepository seatRepository;
	private final PaymentRepository paymentRepository;
	private final ReservationDomainService reservationDomainService;

	@Transactional
	public CreateReservationResult processCreateReservation(ReserveSeatCommand command) throws CustomException {
		QueueToken queueToken = getQueueTokenAndValid(command);
		Concert concert = getConcert(command.concertId());

		ConcertDate concertDate = getConcertDate(command.concertDateId());
		Seat seat = getSeat(command.seatId(), command.concertDateId());

		ReservationDomainResult result = reservationDomainService.processReservation(concert, concertDate, seat, queueToken.userId());

		CreateReservationResult createReservationResult = processReservation(result, queueToken.userId());

		seatHoldRepository.hold(result.seat().id(), queueToken.userId());
		return createReservationResult;
	}

	private CreateReservationResult processReservation(ReservationDomainResult result, UUID userId) {
		Seat savedSeat = seatRepository.save(result.seat());
		Reservation savedReservation = reservationRepository.save(result.reservation());
		Payment savedPayment = paymentRepository.save(Payment.of(userId, savedReservation.id(), savedSeat.price()));

		return new CreateReservationResult(savedReservation, savedPayment, savedSeat, userId);
	}

	private Seat getSeat(UUID seatId, UUID concertDateId) throws CustomException {
		return seatRepository.findBySeatIdAndConcertDateId(seatId, concertDateId)
			.orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
	}

	private ConcertDate getConcertDate(UUID concertDateId) throws CustomException {
		return concertDateRepository.findById(concertDateId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_DATE_NOT_FOUND));
	}

	private Concert getConcert(UUID concertId) throws CustomException {
		return concertRepository.findById(concertId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));
	}

	private QueueToken getQueueTokenAndValid(ReserveSeatCommand command) throws CustomException {
		QueueToken queueToken = queueTokenRepository.findQueueTokenByTokenId(command.queueTokenId());
		QueueTokenUtil.validateActiveQueueToken(queueToken);
		return queueToken;
	}
}
