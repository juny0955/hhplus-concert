package kr.hhplus.be.server.usecase.reservation.interactor;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.ConcertDate;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.event.KafkaEventObject;
import kr.hhplus.be.server.domain.event.reservation.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.usecase.concert.ConcertDateRepository;
import kr.hhplus.be.server.usecase.concert.ConcertRepository;
import kr.hhplus.be.server.usecase.concert.SeatRepository;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.exception.CustomException;
import kr.hhplus.be.server.usecase.exception.ErrorCode;
import kr.hhplus.be.server.usecase.payment.PaymentRepository;
import kr.hhplus.be.server.usecase.queue.QueueTokenRepository;
import kr.hhplus.be.server.usecase.queue.QueueTokenUtil;
import kr.hhplus.be.server.usecase.reservation.ReservationRepository;
import kr.hhplus.be.server.usecase.reservation.SeatLockRepository;
import kr.hhplus.be.server.usecase.reservation.SeatHoldRepository;
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
	private final SeatLockRepository seatLockRepository;
	private final SeatRepository seatRepository;
	private final PaymentRepository paymentRepository;
	private final EventPublisher eventPublisher;
	private final ReservationOutput reservationOutput;

	@Override
	@Transactional
	public void reserveSeat(ReserveSeatCommand command) throws CustomException {
		boolean lock = false;

		try {
			QueueToken queueToken = getQueueTokenAndValid(command);
			validConcert(command.concertId());
			findConcertDateAndValid(command.concertDateId());
			Seat seat = getSeatAndValid(command.seatId(), command.concertDateId());
			lock = acquisitionSeatLock(command.seatId());

			seatRepository.save(seat.reserve());

			Reservation reservation = reservationRepository.save(Reservation.of(queueToken.userId(), seat.id()));
			Payment payment = paymentRepository.save(Payment.of(queueToken.userId(), reservation.id(), seat.price()));
			seatHoldRepository.hold(seat.id(), queueToken.userId());

			reservationOutput.ok(ReserveSeatResult.of(reservation.id(), seat.id(), seat.seatNo(), seat.price(), reservation.status()));

			publishReservationCreateEvent(reservation, queueToken, payment, seat);

		} catch (CustomException e) {
			ErrorCode errorCode = e.getErrorCode();
			log.warn("좌석 예약중 비즈니스 예외 발생 - {}", errorCode.getCode());
			throw e;
		} catch (Exception e) {
			log.error("좌석 예약중 예외 발생 - {}", ErrorCode.INTERNAL_SERVER_ERROR, e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		} finally {
			if (lock)
				seatLockRepository.releaseLock(command.seatId());
		}
	}

	private void publishReservationCreateEvent(Reservation reservation, QueueToken queueToken, Payment payment, Seat seat) {
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

	private boolean acquisitionSeatLock(UUID seatId) throws CustomException {
		if (!seatLockRepository.acquisitionLock(seatId))
			throw new CustomException(ErrorCode.SEAT_LOCK_CONFLICT);

		return true;
	}

	private Seat getSeatAndValid(UUID seatId, UUID concertDateId) throws CustomException {
		Seat seat = seatRepository.findBySeatIdAndConcertDateId(seatId, concertDateId)
			.orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));

		if (!seat.canReserve())
			throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);

		return seat;
	}

	private void findConcertDateAndValid(UUID concertDateId) throws CustomException {
		ConcertDate concertDate = concertDateRepository.findById(concertDateId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_DATE_NOT_FOUND));

		if (!concertDate.checkDeadline())
			throw new CustomException(ErrorCode.OVER_DEADLINE);
	}

	private void validConcert(UUID concertId) throws CustomException {
		if (!concertRepository.existsConcert(concertId))
			throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);
	}

	private QueueToken getQueueTokenAndValid(ReserveSeatCommand command) throws CustomException {
		QueueToken queueToken = queueTokenRepository.findQueueTokenByTokenId(command.queueTokenId());
		QueueTokenUtil.validateQueueToken(queueToken);
		return queueToken;
	}
}
