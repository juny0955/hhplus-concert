package kr.hhplus.be.server.domain.reservation.adapter.out.internal.concert;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.reservation.port.out.SeatHoldQueryPort;
import kr.hhplus.be.server.domain.concert.port.in.seathold.CheckHoldSeatUseCase;
import kr.hhplus.be.server.domain.concert.port.in.seathold.HoldSeatUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HoldSeatAdapter implements SeatHoldQueryPort {

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
