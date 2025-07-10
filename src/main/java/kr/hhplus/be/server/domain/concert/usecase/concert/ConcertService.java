package kr.hhplus.be.server.domain.concert.usecase.concert;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.concert.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.port.in.concert.GetConcertByConcertDateIdUseCase;
import kr.hhplus.be.server.domain.concert.port.in.concert.GetOpenConcertUseCase;
import kr.hhplus.be.server.domain.concert.port.in.concert.SoldOutConcertUseCase;
import kr.hhplus.be.server.domain.concert.port.in.concert.ValidOpenConcertUseCase;
import kr.hhplus.be.server.domain.concert.port.out.concert.GetConcertPort;
import kr.hhplus.be.server.domain.concert.port.out.concert.SaveConcertPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConcertService implements
        GetConcertByConcertDateIdUseCase,
        GetOpenConcertUseCase,
        SoldOutConcertUseCase,
        ValidOpenConcertUseCase {

    private final GetConcertPort getConcertPort;
    private final SaveConcertPort saveConcertPort;

    @Override
    public Concert getConcert(UUID concertDateId) throws CustomException {
        return getConcertPort.getConcertByConcertDateId(concertDateId);
    }

    @Override
    public List<Concert> getOpenConcert() {
        return getConcertPort.getOpenConcerts();
    }

    @Override
    @Transactional
    public Concert soldOutConcert(UUID concertId, LocalDateTime soldOutTime) throws CustomException {
        Concert concert = getConcertPort.getConcert(concertId);
        return saveConcertPort.saveConcert(concert.soldOut(soldOutTime));
    }

    @Override
    public void validOpenConcert(UUID concertId) throws CustomException {
        Concert concert = getConcertPort.getConcert(concertId);
        if (!concert.isOpen())
            throw new CustomException(ErrorCode.CONCERT_NOT_OPEN);
    }
}
