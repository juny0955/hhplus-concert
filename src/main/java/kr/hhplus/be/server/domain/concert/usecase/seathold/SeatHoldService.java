package kr.hhplus.be.server.domain.concert.usecase.seathold;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.port.out.seathold.CheckHoldSeatPort;
import kr.hhplus.be.server.domain.concert.port.out.seathold.HasHoldSeatPort;
import kr.hhplus.be.server.domain.concert.port.out.seathold.HoldSeatPort;
import kr.hhplus.be.server.domain.concert.port.out.seathold.ReleaseSeatHoldPort;
import kr.hhplus.be.server.domain.concert.port.in.seathold.CheckHoldSeatUseCase;
import kr.hhplus.be.server.domain.concert.port.in.seathold.HasHoldSeatUseCase;
import kr.hhplus.be.server.domain.concert.port.in.seathold.HoldSeatUseCase;
import kr.hhplus.be.server.domain.concert.port.in.seathold.ReleaseSeatHoldUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatHoldService implements CheckHoldSeatUseCase,
        HasHoldSeatUseCase,
        HoldSeatUseCase,
        ReleaseSeatHoldUseCase {

    private final CheckHoldSeatPort checkHoldSeatPort;
    private final HasHoldSeatPort hasHoldSeatPort;
    private final HoldSeatPort holdSeatPort;
    private final ReleaseSeatHoldPort releaseSeatHold;

    @Override
    public boolean checkHoldSeat(UUID seatId) {
        return checkHoldSeatPort.checkHoldSeat(seatId);
    }

    @Override
    public void hasHoldSeat(UUID seatId, UUID userId) throws CustomException {
        hasHoldSeatPort.hasHoldSeat(seatId, userId);
    }

    @Override
    public void holdSeat(UUID seatId, UUID userId) {
        holdSeatPort.holdSeat(seatId, userId);
    }

    @Override
    public void releaseSeatHold(UUID seatId, UUID userId) {
        releaseSeatHold.releaseSeatHold(seatId, userId);
    }
}
