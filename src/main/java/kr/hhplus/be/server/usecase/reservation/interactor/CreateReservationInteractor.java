package kr.hhplus.be.server.usecase.reservation.interactor;

import kr.hhplus.be.server.domain.event.reservation.ReservationCreatedEvent;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.infrastructure.persistence.reservation.CreateReservationManager;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.reservation.input.ReservationCreateInput;
import kr.hhplus.be.server.usecase.reservation.input.ReserveSeatCommand;
import kr.hhplus.be.server.usecase.reservation.output.ReservationOutput;
import kr.hhplus.be.server.usecase.reservation.output.ReserveSeatResult;
import kr.hhplus.be.server.infrastructure.persistence.reservation.CreateReservationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateReservationInteractor implements ReservationCreateInput {

	private final CreateReservationManager createReservationManager;
	private final ReservationOutput reservationOutput;
	private final EventPublisher eventPublisher;

	@Override
	public void reserveSeat(ReserveSeatCommand command) throws CustomException {
		try {
			CreateReservationResult createReservationResult = createReservationManager.processCreateReservation(command);

			eventPublisher.publish(ReservationCreatedEvent.from(createReservationResult));
			reservationOutput.ok(ReserveSeatResult.from(createReservationResult));
		} catch (CustomException e) {
			ErrorCode errorCode = e.getErrorCode();
			log.warn("좌석 예약중 비즈니스 예외 발생 - {}, {}", errorCode.getCode(), errorCode.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("좌석 예약중 예외 발생 - {}", ErrorCode.INTERNAL_SERVER_ERROR, e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
