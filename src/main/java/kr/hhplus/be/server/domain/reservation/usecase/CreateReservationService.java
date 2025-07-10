package kr.hhplus.be.server.domain.reservation.usecase;

import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.reservation.domain.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.reservation.port.in.CreateReservationUseCase;
import kr.hhplus.be.server.domain.reservation.port.in.ReserveSeatCommand;
import kr.hhplus.be.server.domain.reservation.port.out.*;
import kr.hhplus.be.server.domain.concert.domain.seat.Seat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateReservationService implements CreateReservationUseCase {

	private final ApplicationEventPublisher eventPublisher;
	private final QueueTokenQueryPort queueTokenQueryPort;
	private final ConcertQueryPort concertQueryPort;
	private final SaveReservationPort saveReservationPort;
	private final SeatQueryPort seatQueryPort;
	private final PaymentQueryPort paymentQueryPort;
	private final SeatHoldQueryPort seatHoldQueryPort;

	@Override
	@DistributedLock(key = "reserve:concert:#command.seatId()")
	@Transactional
	public Reservation createReservation(ReserveSeatCommand command) throws Exception {
		QueueToken queueToken = queueTokenQueryPort.getActiveToken(command.queueTokenId());
		concertQueryPort.validOpenConcert(command.concertId());
		concertQueryPort.validDeadLine(command.concertDateId());

		Seat seat = seatQueryPort.reserveSeat(command.seatId());
		Reservation reservation = saveReservationPort.saveReservation(Reservation.of(queueToken.userId(), seat.id()));
		Payment payment = paymentQueryPort.createPayment(queueToken.userId(), reservation.id(), seat.price());

		seatHoldQueryPort.holdSeat(seat.id(), queueToken.userId());

		eventPublisher.publishEvent(ReservationCreatedEvent.from(reservation, payment, seat, queueToken.userId(), command.concertId()));
		return reservation;
	}
}
