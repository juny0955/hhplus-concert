package kr.hhplus.be.server.application.reservation.usecase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.concert.port.in.ValidOpenConcertInput;
import kr.hhplus.be.server.application.concertDate.port.in.ValidDeadLineInput;
import kr.hhplus.be.server.application.queue.port.in.GetActiveQueueTokenInput;
import kr.hhplus.be.server.application.reservation.dto.CreateReservationResult;
import kr.hhplus.be.server.application.reservation.dto.ReserveSeatResult;
import kr.hhplus.be.server.application.reservation.port.in.ReservationCreateInput;
import kr.hhplus.be.server.application.reservation.port.in.ReserveSeatCommand;
import kr.hhplus.be.server.application.reservation.service.ReservationService;
import kr.hhplus.be.server.config.aop.DistributedLock;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.reservation.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReserveInteractor implements ReservationCreateInput {

	private final static String LOCK_KEY = "reserve:seat:";

	private final ApplicationEventPublisher eventPublisher;
	private final ReservationService reservationService;
	private final GetActiveQueueTokenInput getActiveQueueTokenInput;
	private final ValidOpenConcertInput validOpenConcertInput;
	private final ValidDeadLineInput validDeadLineInput;

	@Override
	@DistributedLock(key = "reserve:seat:#command.seatId()")
	public ReserveSeatResult reserveSeat(ReserveSeatCommand command) throws Exception {
		QueueToken queueToken = getActiveQueueTokenInput.getActiveQueueToken(command.queueTokenId());
		validOpenConcertInput.validOpenConcert(command.concertId());
		validDeadLineInput.validDeadLine(command.concertDateId());

		CreateReservationResult createReservationResult = reservationService.createReservation(command, queueToken);

		eventPublisher.publishEvent(ReservationCreatedEvent.from(createReservationResult));
		return ReserveSeatResult.from(createReservationResult);
	}
}
