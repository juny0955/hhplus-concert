package kr.hhplus.be.server.reservation.application.interactor;

import kr.hhplus.be.server.concert.ports.in.concert.ValidOpenConcertInput;
import kr.hhplus.be.server.concert.ports.in.concertDate.ValidDeadLineInput;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.ports.in.GetActiveQueueTokenInput;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.reservation.application.dto.CreateReservationResult;
import kr.hhplus.be.server.reservation.application.dto.ReserveSeatResult;
import kr.hhplus.be.server.reservation.application.service.ReservationApplicationService;
import kr.hhplus.be.server.reservation.domain.ReservationCreatedEvent;
import kr.hhplus.be.server.reservation.ports.in.ReservationCreateInput;
import kr.hhplus.be.server.reservation.ports.in.ReserveSeatCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReserveInteractor implements ReservationCreateInput {

	private final static String LOCK_KEY = "reserve:seat:";

	private final ApplicationEventPublisher eventPublisher;
	private final DistributedLockManager distributedLockManager;
	private final ReservationApplicationService reservationApplicationService;
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
			() -> reservationApplicationService.createReservation(command, queueToken)
		);

		eventPublisher.publishEvent(ReservationCreatedEvent.from(createReservationResult));
		return ReserveSeatResult.from(createReservationResult);
	}
}
