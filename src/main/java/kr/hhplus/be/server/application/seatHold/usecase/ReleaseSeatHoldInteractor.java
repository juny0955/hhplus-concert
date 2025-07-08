package kr.hhplus.be.server.application.seatHold.usecase;

import kr.hhplus.be.server.application.seatHold.service.SeatHoldService;
import kr.hhplus.be.server.application.seatHold.port.in.ReleaseSeatHoldUseCase;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReleaseSeatHoldInteractor implements ReleaseSeatHoldUseCase {

    private final SeatHoldService seatHoldService;

    @Override
    public void releaseSeatHold(UUID seatId, UUID userId) throws CustomException {
        seatHoldService.releaseSeatHold(seatId, userId);
    }
}
