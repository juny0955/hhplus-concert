package kr.hhplus.be.server.reservation.application.service;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.ports.in.seat.ReserveSeatInput;
import kr.hhplus.be.server.concert.ports.out.ConcertDateRepository;
import kr.hhplus.be.server.concert.ports.out.ConcertRepository;
import kr.hhplus.be.server.concert.ports.out.SeatHoldRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.ports.in.CreatePaymentInput;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.ports.in.GetActiveQueueTokenInput;
import kr.hhplus.be.server.reservation.application.dto.CreateReservationResult;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.ports.in.ReserveSeatCommand;
import kr.hhplus.be.server.reservation.ports.out.ReservationRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateReservationManager {

	private final ReservationRepository reservationRepository;
	private final GetActiveQueueTokenInput getActiveQueueTokenInput;
	private final ConcertRepository concertRepository;
	private final ConcertDateRepository concertDateRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final ReserveSeatInput reserveSeatInput;
	private final CreatePaymentInput createPaymentInput;

	@Transactional
	public CreateReservationResult processCreateReservation(ReserveSeatCommand command) throws CustomException {
		QueueToken queueToken = getActiveQueueTokenInput.getActiveQueueToken(command.queueTokenId());
		Concert concert = getConcert(command.concertId());

		ConcertDate concertDate = getConcertDate(command.concertDateId());

		validateConcertOpenTimeAndDeadline(concert);
		validateConcertDateDeadline(concertDate);

		Seat savedSeat = reserveSeatInput.reserveSeat(command.seatId());
		Reservation savedReservation = reservationRepository.save(Reservation.of(queueToken.userId(), savedSeat.id()));
		Payment savedPayment = createPaymentInput.createPayment(queueToken.userId(), savedReservation.id(), savedSeat.price());

		seatHoldRepository.hold(savedSeat.id(), queueToken.userId());
		return new CreateReservationResult(savedReservation, savedPayment, savedSeat, queueToken.userId());
	}

	private ConcertDate getConcertDate(UUID concertDateId) throws CustomException {
		return concertDateRepository.findById(concertDateId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_DATE_NOT_FOUND));
	}

	private Concert getConcert(UUID concertId) throws CustomException {
		return concertRepository.findById(concertId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));
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
