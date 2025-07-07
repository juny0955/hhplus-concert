package kr.hhplus.be.server.concert.application.service.seat;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.Seats;
import kr.hhplus.be.server.concert.ports.out.ConcertRepository;
import kr.hhplus.be.server.concert.ports.out.SeatRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatApplicationService {

	private final SeatRepository seatRepository;
	private final ConcertRepository concertRepository;

	public Seat getSeat(UUID seatId) throws CustomException {
		return seatRepository.findById(seatId).orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
	}

	public List<Seat> getAvailableSeats(UUID concertId, UUID concertDateId) throws CustomException {
		existsConcert(concertId);

		Seats availableSeats = seatRepository.findAvailableSeats(concertId, concertDateId);

		if (availableSeats.seats().isEmpty()) {
			existsConcertDate(concertDateId);

			log.debug("콘서트 예약 가능 좌석 조회 - 없음: CONCERT_DATE_ID - {}", concertDateId);
			return Collections.emptyList();
		}

		return availableSeats.seats();
	}

	@Transactional
	public Seat paidSeat(UUID seatId) throws CustomException {
		Seat seat = getSeat(seatId);
		return seatRepository.save(seat.payment());
	}

	@Transactional
	public Seat reserveSeat(UUID seatId) throws CustomException {
		Seat seat = getSeat(seatId);
		return seatRepository.save(seat.reserve());
	}
}
