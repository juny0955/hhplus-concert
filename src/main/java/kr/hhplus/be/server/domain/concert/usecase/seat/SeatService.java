package kr.hhplus.be.server.domain.concert.usecase.seat;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.domain.seat.Seat;
import kr.hhplus.be.server.domain.concert.domain.seat.Seats;
import kr.hhplus.be.server.domain.concert.port.in.seat.*;
import kr.hhplus.be.server.domain.concert.port.out.concert.GetConcertPort;
import kr.hhplus.be.server.domain.concert.port.out.concertDate.GetConcertDatePort;
import kr.hhplus.be.server.domain.concert.port.out.seat.GetSeatPort;
import kr.hhplus.be.server.domain.concert.port.out.seat.SaveSeatPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatService implements
        ExpireSeatUseCase,
        GetAvailableSeatsUseCase,
        GetSeatsByConcertDateIdUseCase,
        PaidSeatUseCase,
        ReserveSeatUseCase {

    private final GetSeatPort getSeatPort;
    private final SaveSeatPort saveSeatPort;
    private final GetConcertPort getConcertPort;
    private final GetConcertDatePort getConcertDatePort;

    @Override
    @Transactional
    public Seat expireSeat(UUID seatId) throws CustomException {
        Seat seat = getSeatPort.getSeat(seatId);
        return saveSeatPort.saveSeat(seat.expire());
    }

    @Override
    public List<Seat> getAvailableSeats(UUID concertId, UUID concertDateId) throws CustomException {
        getConcertPort.existsConcert(concertId);

        Seats availableSeats = getSeatPort.getAvailableSeat(concertId, concertDateId);

        if (availableSeats.seats().isEmpty()) {
            getConcertDatePort.existsConcertDate(concertDateId);

            log.debug("콘서트 예약 가능 좌석 조회 - 없음: CONCERT_DATE_ID - {}", concertDateId);
            return Collections.emptyList();
        }

        return availableSeats.seats();
    }

    @Override
    public List<Seat> getSeatsByConcertDateId(UUID concertDateId) {
        return getSeatPort.getSeatsByConcertDateId(concertDateId);
    }

    @Override
    @Transactional
    public Seat paidSeat(UUID seatId) throws CustomException {
        Seat seat = getSeatPort.getSeat(seatId);
        return saveSeatPort.saveSeat(seat.payment());
    }

    @Override
    @Transactional
    public Seat reserveSeat(UUID seatId) throws CustomException {
        Seat seat = getSeatPort.getSeat(seatId);
        return saveSeatPort.saveSeat(seat.reserve());
    }
}
