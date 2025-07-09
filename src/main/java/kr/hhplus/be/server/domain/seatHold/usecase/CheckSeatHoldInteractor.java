package kr.hhplus.be.server.domain.seatHold.usecase;

import kr.hhplus.be.server.domain.seatHold.service.SeatHoldService;
import kr.hhplus.be.server.domain.seatHold.port.in.CheckSeatHoldUseCase;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CheckSeatHoldInteractor implements CheckSeatHoldUseCase {

    private final SeatHoldService seatHoldService;

    @Override
    public void checkSeatHold(UUID seatId, UUID userId) throws CustomException {
        seatHoldService.isHoldByUser(seatId, userId);
    }
}
