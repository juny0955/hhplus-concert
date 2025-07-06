package kr.hhplus.be.server.concert.usecase;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.concert.domain.concert.ConcertRepository;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDateRepository;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.SeatRepository;
import kr.hhplus.be.server.concert.domain.seat.Seats;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ConcertService {

	private final ConcertRepository concertRepository;
	private final ConcertDateRepository concertDateRepository;
	private final SeatRepository seatRepository;

	public List<ConcertDate> getAvailableConcertDates(UUID concertId) throws CustomException {
		existsConcert(concertId);
		return concertDateRepository.findAvailableDatesWithAvailableSeatCount(concertId).concertDates();
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

	private void existsConcert(UUID concertId) throws CustomException {
		if (!concertRepository.existsById(concertId)) {
			log.warn("콘서트 조회 실패: CONCERT_ID - {}", concertId);
			throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);
		}

		log.debug("콘서트 조회: CONCERT_ID - {}", concertId);
	}

	private void existsConcertDate(UUID concertDateId) throws CustomException {
		if (!concertDateRepository.existsById(concertDateId)) {
			log.warn("콘서트 예약 가능 좌석 조회 실패: CONCERT_DATE_ID - {}", concertDateId);
			throw new CustomException(ErrorCode.CANNOT_RESERVATION_DATE);
		}
	}
}
