package kr.hhplus.be.server.usecase.reservation.interactor;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.event.reservation.ReservationCreatedEvent;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.reservation.input.ReservationInput;
import kr.hhplus.be.server.usecase.reservation.input.ReserveSeatCommand;
import kr.hhplus.be.server.usecase.reservation.output.ReservationOutput;
import kr.hhplus.be.server.usecase.reservation.output.ReserveSeatResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationInteractor implements ReservationInput {

	private final ReservationTransactionService reservationTransactionService;
	private final ReservationOutput reservationOutput;
	private final EventPublisher eventPublisher;

	@Override
	public void reserveSeat(ReserveSeatCommand command) throws CustomException {
		try {
			ReservationTransactionResult reservationTransactionResult = reservationTransactionService.processReservationTransaction(command);

			eventPublisher.publish(ReservationCreatedEvent.from(reservationTransactionResult));
			reservationOutput.ok(ReserveSeatResult.from(reservationTransactionResult));
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
