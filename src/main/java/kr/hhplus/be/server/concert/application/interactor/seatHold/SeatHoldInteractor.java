package kr.hhplus.be.server.concert.application.interactor.seatHold;

import kr.hhplus.be.server.concert.application.service.SeatHoldApplicationService;
import kr.hhplus.be.server.concert.ports.in.seatHold.SeatHoldInput;
import kr.hhplus.be.server.framework.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatHoldInteractor implements SeatHoldInput {

    private final SeatHoldApplicationService seatHoldApplicationService;

    @Override
    public void seatHold(UUID seatId, UUID userId) throws CustomException {
        seatHoldApplicationService.holdSeat(seatId, userId);
    }
}
