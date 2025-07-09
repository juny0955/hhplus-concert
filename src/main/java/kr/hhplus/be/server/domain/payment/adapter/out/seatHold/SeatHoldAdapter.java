package kr.hhplus.be.server.domain.payment.adapter.out.seatHold;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.port.out.CheckSeatHoldPort;
import kr.hhplus.be.server.domain.payment.port.out.ReleaseSeatHoldPort;
import kr.hhplus.be.server.domain.seatHold.port.in.CheckSeatHoldUseCase;
import kr.hhplus.be.server.domain.seatHold.port.in.ReleaseSeatHoldUseCase;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatHoldAdapter implements CheckSeatHoldPort, ReleaseSeatHoldPort {

	private final CheckSeatHoldUseCase checkSeatHoldUseCase;
	private final ReleaseSeatHoldUseCase releaseSeatHoldInput;

	@Override
	public void checkSeatHold(UUID seatId, UUID userId) throws CustomException {
		checkSeatHoldUseCase.checkSeatHold(seatId, userId);
	}

	@Override
	public void releaseSeatHold(UUID seatId, UUID userId) throws CustomException {
		releaseSeatHoldInput.releaseSeatHold(seatId, userId);
	}
}
