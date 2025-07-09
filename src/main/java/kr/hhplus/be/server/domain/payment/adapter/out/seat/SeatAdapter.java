package kr.hhplus.be.server.domain.payment.adapter.out.seat;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.port.out.PaidSeatPort;
import kr.hhplus.be.server.domain.seat.port.in.PaidSeatUseCase;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.common.exception.CustomException;
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
