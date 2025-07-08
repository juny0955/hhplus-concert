package kr.hhplus.be.server.application.seat.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.seat.port.in.ExpireSeatInput;
import kr.hhplus.be.server.application.seat.service.SeatService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpireSeatInteractor implements ExpireSeatInput {

	private final SeatService seatService;

	@Override
	public Seat expireSeat(UUID seatId) throws CustomException {
		return seatService.expireSeat(seatId);
	}
}
