package kr.hhplus.be.server.domain.reservation.usecase;

import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.payment.port.out.GetActiveTokenPort;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.reservation.domain.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.reservation.port.in.CreateReservationUseCase;
import kr.hhplus.be.server.domain.reservation.port.in.ReserveSeatCommand;
import kr.hhplus.be.server.domain.reservation.port.out.*;
import kr.hhplus.be.server.domain.seat.domain.Seat;
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
	private final GetActiveTokenPort getActiveTokenPort;
	private final ValidOpenConcertPort validOpenConcertPort;
	private final ValidDeadLinePort validDeadLinePort;
	private final SaveReservationPort saveReservationPort;
	private final ReserveSeatPort reserveSeatPort;
	private final CreatePaymentPort createPaymentPort;
	private final HoldSeatPort holdSeatPort;

	@Override
	@DistributedLock(key = "reserve:seat:#command.seatId()")
	@Transactional
	public Reservation createReservation(ReserveSeatCommand command) throws Exception {
		QueueToken queueToken = getActiveTokenPort.getActiveToken(command.queueTokenId());
		validOpenConcertPort.validOpenConcert(command.concertId());
		validDeadLinePort.validDeadLine(command.concertDateId());

		Seat seat = reserveSeatPort.reserveSeat(command.seatId());
		Reservation reservation = saveReservationPort.saveReservation(Reservation.of(queueToken.userId(), seat.id()));
		Payment payment 	 = createPaymentPort.createPayment(queueToken.userId(), reservation.id(), seat.price());

		holdSeatPort.holdSeat(seat.id(), queueToken.userId());

		eventPublisher.publishEvent(ReservationCreatedEvent.from(reservation, payment, seat, queueToken.userId(), command.concertId()));
		return reservation;
	}
}
