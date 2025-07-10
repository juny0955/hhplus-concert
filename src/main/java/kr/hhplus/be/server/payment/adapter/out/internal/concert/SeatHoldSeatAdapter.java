package kr.hhplus.be.server.payment.adapter.out.internal.concert;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.payment.port.out.SeatHoldQueryPort;
import kr.hhplus.be.server.reservation.port.in.seathold.HasHoldSeatUseCase;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatHoldSeatAdapter implements SeatHoldQueryPort {

	private final HasHoldSeatUseCase hasHoldSeatUseCase;

	@Override
	public void hasSeatHold(UUID seatId, UUID userId) throws CustomException {
		hasHoldSeatUseCase.hasHoldSeat(seatId, userId);
	}
}
