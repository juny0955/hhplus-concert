package kr.hhplus.be.server.payment.adapter.out.internal.concert;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.payment.port.out.SeatHoldQueryPort;
import kr.hhplus.be.server.reservation.port.in.seathold.HasHoldSeatUseCase;
import kr.hhplus.be.server.reservation.port.in.seathold.ReleaseSeatHoldUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatHoldSeatAdapter implements SeatHoldQueryPort {

	private final HasHoldSeatUseCase hasHoldSeatUseCase;
	private final ReleaseSeatHoldUseCase releaseSeatHoldUseCase;

	@Override
	public void hasSeatHold(UUID seatId, UUID userId) throws CustomException {
		hasHoldSeatUseCase.hasHoldSeat(seatId, userId);
	}

	@Override
	public void releaseSeatHold(UUID seatId) throws CustomException {
		releaseSeatHoldUseCase.releaseSeatHold(seatId);
	}
}
