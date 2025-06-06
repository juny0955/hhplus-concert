package kr.hhplus.be.server.usecase.reservation.interactor;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.ConcertDate;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.event.KafkaEventObject;
import kr.hhplus.be.server.domain.event.reservation.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.queue.QueueStatus;
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
import kr.hhplus.be.server.usecase.reservation.ReservationRepository;
import kr.hhplus.be.server.usecase.reservation.SeatLockRepository;
import kr.hhplus.be.server.usecase.reservation.SeatHoldRepository;
import kr.hhplus.be.server.usecase.reservation.input.ReservationInput;
import kr.hhplus.be.server.usecase.reservation.input.ReserveSeatCommand;
import kr.hhplus.be.server.usecase.reservation.output.ReservationOutput;
import kr.hhplus.be.server.usecase.reservation.output.ReserveSeatResult;
import lombok.RequiredArgsConstructor;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationInteractor implements ReservationInput {

	private static final Logger log = LoggerFactory.getLogger(ReservationInteractor.class);
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
			QueueToken queueToken = queueTokenRepository.findQueueTokenByTokenId(command.queueTokenId());
			if (queueToken == null || !queueToken.status().equals(QueueStatus.ACTIVE))
				throw new CustomException(ErrorCode.INVALID_QUEUE_TOKEN);

			if (!concertRepository.existsConcert(command.concertId()))
				throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);

			ConcertDate concertDate = concertDateRepository.findById(command.concertDateId())
				.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_DATE_NOT_FOUND));

			if (!concertDate.checkDeadline())
				throw new CustomException(ErrorCode.OVER_DEADLINE);

			Seat seat = seatRepository.findById(command.seatId())
				.orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));

			if (!seat.canReserve())
				throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);

			if (!seatLockRepository.getLock(command.seatId()))
				throw new CustomException(ErrorCode.SEAT_LOCK_CONFLICT);
			lock = true;

			seatRepository.save(seat.reserve());

			LocalDateTime now = LocalDateTime.now();

			Reservation reservation = reservationRepository.save(Reservation.of(queueToken.userId(), seat.id(), now));
			Payment payment = paymentRepository.save(
				Payment.of(queueToken.userId(), reservation.id(), seat.price(), now));

			seatHoldRepository.hold(seat.id());

			reservationOutput.ok(
				ReserveSeatResult.of(reservation.id(), seat.id(), seat.seatNo(), seat.price(), reservation.status(),
					now));

			ReservationCreatedEvent reservationCreatedEvent = ReservationCreatedEvent.of(
				reservation.id(),
				queueToken.userId(),
				payment.id(),
				seat.id(),
				payment.amount(),
				now
			);
			KafkaEventObject<ReservationCreatedEvent> event = KafkaEventObject.from(reservationCreatedEvent);
			eventPublisher.publish(event);
		} catch (CustomException e) {
			ErrorCode errorCode = e.getErrorCode();
			log.warn("좌석 예약중 에러 발생 - {}", errorCode.getCode());
			throw e;
		} catch (Exception e) {
			log.error("좌석 예약중 에러 발생 - {}", ErrorCode.INTERNAL_SERVER_ERROR, e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		} finally {
			if (lock)
				seatLockRepository.releaseLock(command.seatId());
		}
	}
}
