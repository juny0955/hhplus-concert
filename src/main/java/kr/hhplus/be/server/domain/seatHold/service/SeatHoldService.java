package kr.hhplus.be.server.domain.seatHold.service;

import kr.hhplus.be.server.domain.seatHold.port.out.SeatHoldRepository;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatHoldService {

    private final SeatHoldRepository seatHoldRepository;

    public void isHoldByUser(UUID seatId, UUID userId) throws CustomException {
        if (!seatHoldRepository.hasHoldByUser(seatId, userId))
            throw new CustomException(ErrorCode.SEAT_NOT_HOLD);
    }

    public void releaseSeatHold(UUID seatId, UUID userId) throws CustomException {
        isHoldByUser(seatId, userId);
        seatHoldRepository.deleteHold(seatId, userId);
    }

    public void holdSeat(UUID seatId, UUID userId) throws CustomException {
        if (seatHoldRepository.isHoldSeat(seatId))
            throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);

        seatHoldRepository.hold(seatId, userId);
    }
}
