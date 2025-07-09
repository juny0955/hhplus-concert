package kr.hhplus.be.server.domain.payment.adapter.out.seatHold;

import java.util.UUID;

import kr.hhplus.be.server.domain.seatHold.port.in.HasHoldSeatUseCase;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.port.out.HashHoldSeatPort;
import kr.hhplus.be.server.domain.payment.port.out.ReleaseSeatHoldPort;
import kr.hhplus.be.server.domain.seatHold.port.in.ReleaseSeatHoldUseCase;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatHoldSeatAdapter implements HashHoldSeatPort, ReleaseSeatHoldPort {

	private final HasHoldSeatUseCase hasHoldSeatUseCase;
	private final ReleaseSeatHoldUseCase releaseSeatHoldInput;

	@Override
	public void hasHoldSeat(UUID seatId, UUID userId) throws CustomException {
		hasHoldSeatUseCase.hasHoldSeat(seatId, userId);
	}

	@Override
	public void releaseSeatHold(UUID seatId, UUID userId) throws CustomException {
		releaseSeatHoldInput.releaseSeatHold(seatId, userId);
	}
}
