package kr.hhplus.be.server.domain.seatHold.usecase;

import kr.hhplus.be.server.domain.seatHold.port.in.CheckHoldSeatUseCase;
import kr.hhplus.be.server.domain.seatHold.port.out.CheckHoldSeatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckHoldSeatService implements CheckHoldSeatUseCase {

    private final CheckHoldSeatPort checkHoldSeatPort;

    @Override
    public boolean checkHoldSeat(UUID seatId) {
        return checkHoldSeatPort.checkHoldSeat(seatId);
    }
}
