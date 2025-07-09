package kr.hhplus.be.server.domain.concert.usecase;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.concert.port.in.SoldOutConcertUseCase;
import kr.hhplus.be.server.domain.concert.port.out.GetConcertPort;
import kr.hhplus.be.server.domain.concert.port.out.SaveConcertPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SoldOutConcertService implements SoldOutConcertUseCase {

    private final GetConcertPort getConcertPort;
    private final SaveConcertPort saveConcertPort;

    @Override
    @Transactional
    public Concert soldOutConcert(UUID concertId, LocalDateTime soldOutTime) throws CustomException {
        Concert concert = getConcertPort.getConcert(concertId);
        return saveConcertPort.saveConcert(concert.soldOut(soldOutTime));
    }
}
