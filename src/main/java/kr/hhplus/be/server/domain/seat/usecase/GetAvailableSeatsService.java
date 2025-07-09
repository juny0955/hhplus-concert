package kr.hhplus.be.server.domain.seat.usecase;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concert.port.out.GetConcertPort;
import kr.hhplus.be.server.domain.concertDate.port.out.GetConcertDatePort;
import kr.hhplus.be.server.domain.seat.port.in.GetAvailableSeatsUseCase;
import kr.hhplus.be.server.domain.seat.port.out.SeatRepository;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.seat.domain.Seats;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetAvailableSeatsService implements GetAvailableSeatsUseCase {

	private final SeatRepository seatRepository;
	private final GetConcertPort getConcertPort;
	private final GetConcertDatePort getConcertDatePort;

	@Override
	public List<Seat> getAvailableSeats(UUID concertId, UUID concertDateId) throws CustomException {
		getConcertPort.existsConcert(concertId);

		Seats availableSeats = seatRepository.findAvailableSeats(concertId, concertDateId);

		if (availableSeats.seats().isEmpty()) {
			getConcertDatePort.existsConcertDate(concertDateId);

			log.debug("콘서트 예약 가능 좌석 조회 - 없음: CONCERT_DATE_ID - {}", concertDateId);
			return Collections.emptyList();
		}

		return availableSeats.seats();
	}
}
