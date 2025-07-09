package kr.hhplus.be.server.domain.soldoutRank.adapter.out.concert;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.concert.port.in.SoldOutConcertUseCase;
import kr.hhplus.be.server.domain.soldoutRank.port.out.GetConcertPort;
import kr.hhplus.be.server.domain.soldoutRank.port.out.SoldOutConcertPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConcertAdepter implements GetConcertPort, SoldOutConcertPort {

    private final GetConcertPort getConcertPort;
    private final SoldOutConcertUseCase soldOutConcertUseCase;

    @Override
    public Concert getConcertByConcertDateId(UUID concertDateId) {
        return getConcertPort.getConcertByConcertDateId(concertDateId);
    }

    @Override
    public Concert soldOutConcert(UUID concertId, LocalDateTime soldOutTime) throws CustomException {
        return soldOutConcertUseCase.soldOutConcert(concertId, soldOutTime);
    }
}
