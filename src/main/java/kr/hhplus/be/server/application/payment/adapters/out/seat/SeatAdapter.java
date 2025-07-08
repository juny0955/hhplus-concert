package kr.hhplus.be.server.application.payment.adapters.out.seat;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.payment.port.out.PaidSeatPort;
import kr.hhplus.be.server.application.seat.port.in.PaidSeatUseCase;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatAdapter implements PaidSeatPort {

	private final PaidSeatUseCase paidSeatUseCase;

	@Override
	public Seat paidSeat(UUID seatId) throws CustomException {
		return paidSeatUseCase.paidSeat(seatId);
	}
}
