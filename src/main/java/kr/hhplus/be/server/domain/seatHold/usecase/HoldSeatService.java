package kr.hhplus.be.server.domain.seatHold.usecase;

import kr.hhplus.be.server.domain.seatHold.port.in.HoldSeatUseCase;
import kr.hhplus.be.server.domain.seatHold.port.out.HoldSeatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HoldSeatService implements HoldSeatUseCase {

    private final HoldSeatPort holdSeatPort;

    @Override
    public void holdSeat(UUID seatId, UUID userId) {
        holdSeatPort.holdSeat(seatId, userId);
    }
}
