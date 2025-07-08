package kr.hhplus.be.server.application.seatHold.usecase;

import kr.hhplus.be.server.application.seatHold.service.SeatHoldService;
import kr.hhplus.be.server.application.seatHold.port.in.CheckSeatHoldInput;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CheckSeatHoldInteractor implements CheckSeatHoldInput {

    private final SeatHoldService seatHoldService;

    @Override
    public void checkSeatHold(UUID seatId, UUID userId) throws CustomException {
        seatHoldService.isHoldByUser(seatId, userId);
    }
}
