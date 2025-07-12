package kr.hhplus.be.server.concert.usecase.seat;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.Seats;
import kr.hhplus.be.server.concert.port.in.seat.ExpireSeatUseCase;
import kr.hhplus.be.server.concert.port.in.seat.GetAvailableSeatsUseCase;
import kr.hhplus.be.server.concert.port.in.seat.PaidSeatUseCase;
import kr.hhplus.be.server.concert.port.in.seat.ReserveSeatUseCase;
import kr.hhplus.be.server.concert.port.out.concert.GetConcertPort;
import kr.hhplus.be.server.concert.port.out.concertDate.GetConcertDatePort;
import kr.hhplus.be.server.concert.port.out.queue.QueueTokenRepository;
import kr.hhplus.be.server.concert.port.out.seat.GetSeatPort;
import kr.hhplus.be.server.concert.port.out.seat.SaveSeatPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatService implements
        ExpireSeatUseCase,
        GetAvailableSeatsUseCase,
        PaidSeatUseCase,
        ReserveSeatUseCase {

    private final GetSeatPort getSeatPort;
    private final SaveSeatPort saveSeatPort;
    private final GetConcertPort getConcertPort;
    private final GetConcertDatePort getConcertDatePort;
    private final QueueTokenRepository queueTokenRepository;

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
    @Transactional
    public Seat paidSeat(UUID seatId, UUID tokenId) throws CustomException {
        Seat seat = getSeatPort.getSeat(seatId);
        Seat paidSeat = saveSeatPort.saveSeat(seat.payment());
        queueTokenRepository.expiresQueueToken(tokenId.toString());
        return paidSeat;
    }

    @Override
    @Transactional
    public Seat reserveSeat(UUID seatId, UUID concertId, UUID concertDateId) throws CustomException {
        Concert concert = getConcertPort.getConcert(concertId);
        if (!concert.isOpen())
            throw new CustomException(ErrorCode.CONCERT_NOT_OPEN);

        ConcertDate concertDate = getConcertDatePort.getConcertDate(concertDateId);
        if (concertDate.checkDeadline())
            throw new CustomException(ErrorCode.OVER_DEADLINE);

        Seat seat = getSeatPort.getSeat(seatId);
        return saveSeatPort.saveSeat(seat.reserve());
    }
}
