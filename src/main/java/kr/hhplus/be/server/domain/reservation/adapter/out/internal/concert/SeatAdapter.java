package kr.hhplus.be.server.domain.reservation.adapter.out.internal.concert;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.reservation.port.out.SeatQueryPort;
import kr.hhplus.be.server.domain.concert.domain.seat.Seat;
import kr.hhplus.be.server.domain.concert.port.in.seat.ExpireSeatUseCase;
import kr.hhplus.be.server.domain.concert.port.in.seat.ReserveSeatUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatAdapter implements SeatQueryPort {

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
