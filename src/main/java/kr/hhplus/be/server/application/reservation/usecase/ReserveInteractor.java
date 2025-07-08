package kr.hhplus.be.server.application.reservation.usecase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.adapters.out.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.application.concert.port.in.ValidOpenConcertInput;
import kr.hhplus.be.server.application.concertDate.port.in.ValidDeadLineInput;
import kr.hhplus.be.server.application.queue.port.in.GetActiveQueueTokenInput;
import kr.hhplus.be.server.application.reservation.dto.CreateReservationResult;
import kr.hhplus.be.server.application.reservation.dto.ReserveSeatResult;
import kr.hhplus.be.server.application.reservation.port.in.ReservationCreateInput;
import kr.hhplus.be.server.application.reservation.port.in.ReserveSeatCommand;
import kr.hhplus.be.server.application.reservation.service.ReservationService;
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
	private final DistributedLockManager distributedLockManager;
	private final ReservationService reservationService;
	private final GetActiveQueueTokenInput getActiveQueueTokenInput;
	private final ValidOpenConcertInput validOpenConcertInput;
	private final ValidDeadLineInput validDeadLineInput;

	@Override
	public ReserveSeatResult reserveSeat(ReserveSeatCommand command) throws Exception {
		QueueToken queueToken = getActiveQueueTokenInput.getActiveQueueToken(command.queueTokenId());
		validOpenConcertInput.validOpenConcert(command.concertId());
		validDeadLineInput.validDeadLine(command.concertDateId());

		String lockKey = LOCK_KEY + command.seatId();

		// reservation:seat:{seatId} 락 획득 후 좌석 예약 트랜잭션 수행
		// 락 획득하지 못할 시 예외 응답
		CreateReservationResult createReservationResult = distributedLockManager.executeWithSimpleLockHasReturn(
			lockKey,
			() -> reservationService.createReservation(command, queueToken)
		);

		eventPublisher.publishEvent(ReservationCreatedEvent.from(createReservationResult));
		return ReserveSeatResult.from(createReservationResult);
	}
}
