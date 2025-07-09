package kr.hhplus.be.server.domain.seat.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.seat.port.in.ReserveSeatInput;
import kr.hhplus.be.server.domain.seat.service.SeatService;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReserveSeatInteractor implements ReserveSeatInput {

	private final SeatService seatService;

	@Override
	public Seat reserveSeat(UUID seatId) throws CustomException {
		return seatService.reserveSeat(seatId);
	}
}
