package kr.hhplus.be.server.reservation.usecase.seathold;

import java.util.UUID;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.reservation.port.in.seathold.HasHoldSeatUseCase;
import kr.hhplus.be.server.reservation.port.out.seathold.HasHoldSeatPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatHoldService implements
        HasHoldSeatUseCase{

    private final HasHoldSeatPort hasHoldSeatPort;

    @Override
    public void hasHoldSeat(UUID seatId, UUID userId) throws CustomException {
        hasHoldSeatPort.hasHoldSeat(seatId, userId);
    }
}
