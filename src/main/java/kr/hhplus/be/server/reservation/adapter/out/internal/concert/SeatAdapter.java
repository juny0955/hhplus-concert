package kr.hhplus.be.server.reservation.adapter.out.internal.concert;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.port.in.seat.ExpireSeatUseCase;
import kr.hhplus.be.server.concert.port.in.seat.ReserveSeatUseCase;
import kr.hhplus.be.server.reservation.port.out.SeatQueryPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatAdapter implements SeatQueryPort {

    private final ReserveSeatUseCase reserveSeatUseCase;
    private final ExpireSeatUseCase expireSeatUseCase;

    @Override
    public Seat reserveSeat(UUID seatId, UUID concertId, UUID concertDateId) throws CustomException {
        return reserveSeatUseCase.reserveSeat(seatId, concertId, concertDateId);
    }

    @Override
    public Seat expireSeat(UUID seatId) throws CustomException {
        return expireSeatUseCase.expireSeat(seatId);
    }
}
