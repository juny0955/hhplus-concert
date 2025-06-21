package kr.hhplus.be.server.usecase.reservation.interactor;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.event.reservation.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationDomainResult;
import kr.hhplus.be.server.domain.reservation.ReservationDomainService;
import kr.hhplus.be.server.domain.concertDate.ConcertDateRepository;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.queue.QueueTokenRepository;
import kr.hhplus.be.server.domain.queue.QueueTokenUtil;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.seat.SeatHoldRepository;
import kr.hhplus.be.server.usecase.reservation.input.ReservationInput;
import kr.hhplus.be.server.usecase.reservation.input.ReserveSeatCommand;
import kr.hhplus.be.server.usecase.reservation.output.ReservationOutput;
import kr.hhplus.be.server.usecase.reservation.output.ReserveSeatResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ReservationInteractor implements ReservationInput {

	private final ReservationRepository reservationRepository;
	private final QueueTokenRepository queueTokenRepository;
	private final ConcertRepository concertRepository;
	private final ConcertDateRepository concertDateRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final SeatRepository seatRepository;
	private final PaymentRepository paymentRepository;
	private final ReservationOutput reservationOutput;
	private final ReservationDomainService reservationDomainService;
	private final EventPublisher eventPublisher;

	@Override
	public void reserveSeat(ReserveSeatCommand command) throws CustomException {
		try {
			QueueToken queueToken = getQueueTokenAndValid(command);
			checkExistsConcert(command.concertId());

			ConcertDate concertDate = getConcertDate(command.concertDateId());
			Seat seat = getSeat(command.seatId(), command.concertDateId());

			ReservationDomainResult result = reservationDomainService.processReservation(concertDate, seat, queueToken.userId());

			TransactionResult transactionResult = processTransaction(result, queueToken.userId());

			eventPublisher.publish(ReservationCreatedEvent.of(transactionResult.payment, transactionResult.reservation, seat, queueToken.userId()));
			reservationOutput.ok(ReserveSeatResult.of(transactionResult.reservation, seat));
		} catch (CustomException e) {
			ErrorCode errorCode = e.getErrorCode();
			log.warn("좌석 예약중 비즈니스 예외 발생 - {}, {}", errorCode.getCode(), errorCode.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("좌석 예약중 예외 발생 - {}", ErrorCode.INTERNAL_SERVER_ERROR, e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public TransactionResult processTransaction(ReservationDomainResult result, UUID userId) throws CustomException {
		updateSeatStatusReserved(result.seat());
		Reservation savedReservation = reservationRepository.save(result.reservation());
		Payment 	savedPayment 	 = paymentRepository.save(Payment.of(userId, savedReservation.id(), result.seat().price()));

		seatHoldRepository.hold(result.seat().id(), userId);

		return new TransactionResult(savedPayment, savedReservation);
	}

	private void updateSeatStatusReserved(Seat seat) throws CustomException {
		int updateSeat = seatRepository.updateStatusReserved(seat.id());
		if (updateSeat <= 0)
			throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);
	}

	private Seat getSeat(UUID seatId, UUID concertDateId) throws CustomException {
		return seatRepository.findBySeatIdAndConcertDateId(seatId, concertDateId)
			.orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
	}


	private ConcertDate getConcertDate(UUID concertDateId) throws CustomException {
		return concertDateRepository.findById(concertDateId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_DATE_NOT_FOUND));
	}

	private void checkExistsConcert(UUID concertId) throws CustomException {
		if (!concertRepository.existsById(concertId))
			throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);
	}

	private QueueToken getQueueTokenAndValid(ReserveSeatCommand command) throws CustomException {
		QueueToken queueToken = queueTokenRepository.findQueueTokenByTokenId(command.queueTokenId());
		QueueTokenUtil.validateActiveQueueToken(queueToken);
		return queueToken;
	}

	private record TransactionResult (Payment payment, Reservation reservation) {}
}
