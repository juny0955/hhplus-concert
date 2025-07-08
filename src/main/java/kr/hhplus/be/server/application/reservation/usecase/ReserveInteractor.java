package kr.hhplus.be.server.application.reservation.usecase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.payment.port.out.GetActiveTokenPort;
import kr.hhplus.be.server.application.reservation.dto.CreateReservationResult;
import kr.hhplus.be.server.application.reservation.dto.ReserveSeatResult;
import kr.hhplus.be.server.application.reservation.port.in.ReservationCreateUseCase;
import kr.hhplus.be.server.application.reservation.port.in.ReserveSeatCommand;
import kr.hhplus.be.server.application.reservation.port.out.ValidDeadLinePort;
import kr.hhplus.be.server.application.reservation.port.out.ValidOpenConcertPort;
import kr.hhplus.be.server.application.reservation.service.ReservationService;
import kr.hhplus.be.server.config.aop.DistributedLock;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.reservation.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReserveInteractor implements ReservationCreateUseCase {

	private final ApplicationEventPublisher eventPublisher;
	private final ReservationService reservationService;
	private final GetActiveTokenPort getActiveTokenPort;
	private final ValidOpenConcertPort validOpenConcertPort;
	private final ValidDeadLinePort validDeadLinePort;

	@Override
	@DistributedLock(key = "reserve:seat:#command.seatId()")
	public ReserveSeatResult reserveSeat(ReserveSeatCommand command) throws Exception {
		QueueToken queueToken = getActiveTokenPort.getActiveToken(command.queueTokenId());
		validOpenConcertPort.validOpenConcert(command.concertId());
		validDeadLinePort.validDeadLine(command.concertDateId());

		CreateReservationResult createReservationResult = reservationService.createReservation(command, queueToken);

		eventPublisher.publishEvent(ReservationCreatedEvent.from(createReservationResult));
		return ReserveSeatResult.from(createReservationResult);
	}
}
