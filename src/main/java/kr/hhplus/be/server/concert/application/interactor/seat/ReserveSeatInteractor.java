package kr.hhplus.be.server.concert.application.interactor.seat;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.application.service.SeatApplicationService;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.ports.in.seat.ReserveSeatInput;
import kr.hhplus.be.server.common.framework.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReserveSeatInteractor implements ReserveSeatInput {

	private final SeatApplicationService seatApplicationService;

	@Override
	public Seat reserveSeat(UUID seatId) throws CustomException {
		return seatApplicationService.reserveSeat(seatId);
	}
}
