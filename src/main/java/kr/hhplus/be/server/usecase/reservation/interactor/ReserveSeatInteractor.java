package kr.hhplus.be.server.usecase.reservation.interactor;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.event.reservation.ReservationCreatedEvent;
import kr.hhplus.be.server.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.infrastructure.persistence.reservation.CreateReservationManager;
import kr.hhplus.be.server.infrastructure.persistence.reservation.CreateReservationResult;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.reservation.input.ReservationCreateInput;
import kr.hhplus.be.server.usecase.reservation.input.ReserveSeatCommand;
import kr.hhplus.be.server.usecase.reservation.output.ReservationOutput;
import kr.hhplus.be.server.usecase.reservation.output.ReserveSeatResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReserveSeatInteractor implements ReservationCreateInput {

	private final static String LOCK_KEY = "reserve:seat:";

	private final CreateReservationManager createReservationManager;
	private final ReservationOutput reservationOutput;
	private final EventPublisher eventPublisher;
	private final DistributedLockManager distributedLockManager;

	@Override
	public void reserveSeat(ReserveSeatCommand command) throws Exception {
		String lockKey = LOCK_KEY + command.seatId();

		// reservation:seat:{seatId} 락 획득 후 좌석 예약 트랜잭션 수행
		// 락 획득하지 못할 시 예외 응답
		CreateReservationResult createReservationResult = distributedLockManager.executeWithSimpleLockHasReturn(
			lockKey,
			() -> createReservationManager.processCreateReservation(command)
		);

		eventPublisher.publish(ReservationCreatedEvent.from(createReservationResult));
		reservationOutput.ok(ReserveSeatResult.from(createReservationResult));
	}
}
