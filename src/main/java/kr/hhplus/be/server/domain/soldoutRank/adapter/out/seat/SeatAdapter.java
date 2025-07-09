package kr.hhplus.be.server.domain.soldoutRank.adapter.out.seat;

import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.seat.port.in.GetSeatsByConcertDateIdUseCase;
import kr.hhplus.be.server.domain.soldoutRank.port.out.GetSeatsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatAdapter implements GetSeatsPort {

    private final GetSeatsByConcertDateIdUseCase getSeatsByConcertDateIdUseCase;

    @Override
    public List<Seat> getSeats(UUID concertDateId) {
        return getSeatsByConcertDateIdUseCase.getSeatsByConcertDateId(concertDateId);
    }
}
