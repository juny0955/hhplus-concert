package kr.hhplus.be.server.payment.adapter.out.internal.concert;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.port.in.seat.PaidSeatUseCase;
import kr.hhplus.be.server.payment.port.out.SeatQueryPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentSeatAdapter implements SeatQueryPort {

	private final PaidSeatUseCase paidSeatUseCase;

	@Override
	public Seat paidSeat(UUID seatId, UUID tokenId) throws CustomException {
		return paidSeatUseCase.paidSeat(seatId, tokenId);
	}
}
