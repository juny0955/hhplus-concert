package kr.hhplus.be.server.reservation.usecase.seathold;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.reservation.port.in.seathold.HasHoldSeatUseCase;
import kr.hhplus.be.server.reservation.port.in.seathold.ReleaseSeatHoldUseCase;
import kr.hhplus.be.server.reservation.port.out.seathold.HasHoldSeatPort;
import kr.hhplus.be.server.reservation.port.out.seathold.ReleaseSeatHoldPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatHoldService implements
        HasHoldSeatUseCase,
        ReleaseSeatHoldUseCase {

    private final HasHoldSeatPort hasHoldSeatPort;
    private final ReleaseSeatHoldPort releaseSeatHold;

    @Override
    public void hasHoldSeat(UUID seatId, UUID userId) throws CustomException {
        hasHoldSeatPort.hasHoldSeat(seatId, userId);
    }

    @Override
    public void releaseSeatHold(UUID seatId) {
        releaseSeatHold.releaseSeatHold(seatId);
    }
}
