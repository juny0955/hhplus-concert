package kr.hhplus.be.server.concert.usecase.concert;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.concert.port.in.concert.ExistsConcertUseCase;
import kr.hhplus.be.server.concert.port.in.concert.GetConcertByConcertDateIdUseCase;
import kr.hhplus.be.server.concert.port.in.concert.GetOpenConcertUseCase;
import kr.hhplus.be.server.concert.port.in.concert.ValidOpenConcertUseCase;
import kr.hhplus.be.server.concert.port.out.concert.GetConcertPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConcertService implements
        GetConcertByConcertDateIdUseCase,
        GetOpenConcertUseCase,
        ValidOpenConcertUseCase,
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
    public void validOpenConcert(UUID concertId) throws CustomException {
        Concert concert = getConcertPort.getConcert(concertId);
        if (!concert.isOpen())
            throw new CustomException(ErrorCode.CONCERT_NOT_OPEN);
    }

    @Override
    public void existsConcert(UUID concertId) throws CustomException {
        getConcertPort.existsConcert(concertId);
    }
}
