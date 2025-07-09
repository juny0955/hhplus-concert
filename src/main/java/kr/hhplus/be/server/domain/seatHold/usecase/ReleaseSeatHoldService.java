package kr.hhplus.be.server.domain.seatHold.usecase;


import kr.hhplus.be.server.domain.seatHold.port.in.ReleaseSeatHoldUseCase;
import kr.hhplus.be.server.domain.seatHold.port.out.ReleaseSeatHoldPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReleaseSeatHoldService implements ReleaseSeatHoldUseCase {

    private final ReleaseSeatHoldPort releaseSeatHold;

    @Override
    public void releaseSeatHold(UUID seatId, UUID userId) {
        releaseSeatHold.releaseSeatHold(seatId, userId);
    }
}
