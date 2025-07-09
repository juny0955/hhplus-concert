package kr.hhplus.be.server.domain.seat.usecase;

import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.seat.port.in.GetSeatsByConcertDateIdUseCase;
import kr.hhplus.be.server.domain.seat.port.out.GetSeatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetSeatsByConcertDateIdService implements GetSeatsByConcertDateIdUseCase {

    private final GetSeatPort getSeatsByConcertDateIdPort;

    @Override
    public List<Seat> getSeatsByConcertDateId(UUID concertDateId) {
        return getSeatsByConcertDateIdPort.getSeatsByConcertDateId(concertDateId);
    }
}
