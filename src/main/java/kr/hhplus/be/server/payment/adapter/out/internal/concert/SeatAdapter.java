package kr.hhplus.be.server.payment.adapter.out.internal.concert;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.payment.port.out.SeatQueryPort;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.port.in.seat.PaidSeatUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatAdapter implements SeatQueryPort {

	private final PaidSeatUseCase paidSeatUseCase;

	@Override
	public Seat paidSeat(UUID seatId) throws CustomException {
		return paidSeatUseCase.paidSeat(seatId);
	}
}
