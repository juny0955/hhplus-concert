package kr.hhplus.be.server.concert.application.interactor.seat;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.application.service.seat.SeatApplicationService;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.ports.in.seat.GetSeatInput;
import kr.hhplus.be.server.framework.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetSeatInteractor implements GetSeatInput {

	private final SeatApplicationService seatApplicationService;

	@Override
	public Seat getSeat(UUID seatId) throws CustomException {
		return seatApplicationService.getSeat(seatId);
	}
}
