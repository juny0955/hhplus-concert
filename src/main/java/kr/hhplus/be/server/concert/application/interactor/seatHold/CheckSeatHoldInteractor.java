package kr.hhplus.be.server.concert.application.interactor.seatHold;

import kr.hhplus.be.server.concert.application.service.SeatHoldApplicationService;
import kr.hhplus.be.server.concert.ports.in.seatHold.CheckSeatHoldInput;
import kr.hhplus.be.server.common.framework.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CheckSeatHoldInteractor implements CheckSeatHoldInput {

    private final SeatHoldApplicationService seatHoldApplicationService;

    @Override
    public void checkSeatHold(UUID seatId, UUID userId) throws CustomException {
        seatHoldApplicationService.isHoldByUser(seatId, userId);
    }
}
