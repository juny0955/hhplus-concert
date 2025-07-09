package kr.hhplus.be.server.domain.seatHold.usecase;

import kr.hhplus.be.server.domain.seatHold.service.SeatHoldService;
import kr.hhplus.be.server.domain.seatHold.port.in.SeatHoldInput;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatHoldInteractor implements SeatHoldInput {

    private final SeatHoldService seatHoldService;

    @Override
    public void seatHold(UUID seatId, UUID userId) throws CustomException {
        seatHoldService.holdSeat(seatId, userId);
    }
}
