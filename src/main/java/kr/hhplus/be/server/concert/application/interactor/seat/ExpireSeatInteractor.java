package kr.hhplus.be.server.concert.application.interactor.seat;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.application.service.SeatApplicationService;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.ports.in.seat.ExpireSeatInput;
import kr.hhplus.be.server.framework.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpireSeatInteractor implements ExpireSeatInput {

	private final SeatApplicationService seatApplicationService;

	@Override
	public Seat expireSeat(UUID seatId) throws CustomException {
		return seatApplicationService.expireSeat(seatId);
	}
}
