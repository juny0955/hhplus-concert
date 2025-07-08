package kr.hhplus.be.server.concert.application.interactor.seatHold;

import kr.hhplus.be.server.concert.application.service.SeatHoldApplicationService;
import kr.hhplus.be.server.concert.ports.in.seatHold.ReleaseSeatHoldInput;
import kr.hhplus.be.server.framework.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReleaseSeatHoldInteractor implements ReleaseSeatHoldInput {

    private final SeatHoldApplicationService seatHoldApplicationService;

    @Override
    public void releaseSeatHold(UUID seatId, UUID userId) throws CustomException {
        seatHoldApplicationService.releaseSeatHold(seatId, userId);
    }
}
