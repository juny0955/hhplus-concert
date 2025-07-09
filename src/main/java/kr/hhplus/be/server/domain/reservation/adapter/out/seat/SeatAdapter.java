package kr.hhplus.be.server.domain.reservation.adapter.out.seat;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.reservation.port.out.ExpireSeatPort;
import kr.hhplus.be.server.domain.reservation.port.out.ReserveSeatPort;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.seat.port.in.ExpireSeatUseCase;
import kr.hhplus.be.server.domain.seat.port.in.ReserveSeatUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatAdapter implements ReserveSeatPort, ExpireSeatPort {

    private final ReserveSeatUseCase reserveSeatUseCase;
    private final ExpireSeatUseCase expireSeatUseCase;

    @Override
    public Seat reserveSeat(UUID seatId) throws CustomException {
        return reserveSeatUseCase.reserveSeat(seatId);
    }

    @Override
    public Seat expireSeat(UUID seatId) throws CustomException {
        return expireSeatUseCase.expireSeat(seatId);
    }
}
