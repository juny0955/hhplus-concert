package kr.hhplus.be.server.domain.reservation.adapter.out.seatHold;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.reservation.port.out.CheckHoldSeatPort;
import kr.hhplus.be.server.domain.reservation.port.out.HoldSeatPort;
import kr.hhplus.be.server.domain.seatHold.port.in.CheckHoldSeatUseCase;
import kr.hhplus.be.server.domain.seatHold.port.in.HoldSeatUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HoldSeatAdapter implements HoldSeatPort, CheckHoldSeatPort {

    private final HoldSeatUseCase holdSeatUseCase;
    private final CheckHoldSeatUseCase checkHoldSeatUseCase;

    @Override
    public void holdSeat(UUID seatId, UUID userId) throws CustomException {
        holdSeatUseCase.holdSeat(seatId, userId);
    }

    @Override
    public boolean checkHoldSeat(UUID seatId) {
        return checkHoldSeatUseCase.checkHoldSeat(seatId);
    }
}
