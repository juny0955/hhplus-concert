package kr.hhplus.be.server.application.seatHold.usecase;

import kr.hhplus.be.server.application.seatHold.service.SeatHoldService;
import kr.hhplus.be.server.application.seatHold.port.in.ReleaseSeatHoldInput;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReleaseSeatHoldInteractor implements ReleaseSeatHoldInput {

    private final SeatHoldService seatHoldService;

    @Override
    public void releaseSeatHold(UUID seatId, UUID userId) throws CustomException {
        seatHoldService.releaseSeatHold(seatId, userId);
    }
}
