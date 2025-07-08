package kr.hhplus.be.server.concert.application.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.Seats;
import kr.hhplus.be.server.concert.ports.in.concert.ExistsConcertInput;
import kr.hhplus.be.server.concert.ports.in.concertDate.ExistsConcertDateInput;
import kr.hhplus.be.server.concert.ports.out.SeatRepository;
import kr.hhplus.be.server.common.framework.exception.CustomException;
import kr.hhplus.be.server.common.framework.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatApplicationService {

	private final SeatRepository seatRepository;
	private final ExistsConcertInput existsConcertInput;
	private final ExistsConcertDateInput existsConcertDateInput;

	public Seat getSeat(UUID seatId) throws CustomException {
		return seatRepository.findById(seatId).orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
	}

	public List<Seat> getAvailableSeats(UUID concertId, UUID concertDateId) throws CustomException {
		existsConcertInput.existsConcert(concertId);

		Seats availableSeats = seatRepository.findAvailableSeats(concertId, concertDateId);

		if (availableSeats.seats().isEmpty()) {
			existsConcertDateInput.existsConcertDate(concertDateId);

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

	@Transactional
	public Seat expireSeat(UUID seatId) throws CustomException {
		Seat seat = getSeat(seatId);
		return seatRepository.save(seat.expired());
	}
}
