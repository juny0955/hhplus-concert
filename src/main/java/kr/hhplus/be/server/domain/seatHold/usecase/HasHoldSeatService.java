package kr.hhplus.be.server.domain.seatHold.usecase;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.seatHold.port.in.HasHoldSeatUseCase;
import kr.hhplus.be.server.domain.seatHold.port.out.HasHoldSeatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HasHoldSeatService implements HasHoldSeatUseCase {

    private final HasHoldSeatPort hasHoldSeatPort;

    @Override
    public void hasHoldSeat(UUID seatId, UUID userId) throws CustomException {
        hasHoldSeatPort.hasHoldSeat(seatId, userId);
    }
}
