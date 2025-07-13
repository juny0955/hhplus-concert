package kr.hhplus.be.server.concert.usecase.concert;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.concert.port.in.concert.ExistsConcertUseCase;
import kr.hhplus.be.server.concert.port.in.concert.GetConcertByConcertDateIdUseCase;
import kr.hhplus.be.server.concert.port.in.concert.GetOpenConcertUseCase;
import kr.hhplus.be.server.concert.port.out.concert.GetConcertPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConcertService implements
        GetConcertByConcertDateIdUseCase,
        GetOpenConcertUseCase,
        ExistsConcertUseCase {

    private final GetConcertPort getConcertPort;

    @Override
    public Concert getConcert(UUID concertDateId) throws CustomException {
        return getConcertPort.getConcertByConcertDateId(concertDateId);
    }

    @Override
    public List<Concert> getOpenConcert() {
        return getConcertPort.getOpenConcerts();
    }

    @Override
    public void existsConcert(UUID concertId) throws CustomException {
        getConcertPort.existsConcert(concertId);
    }
}
