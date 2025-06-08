package kr.hhplus.be.server.usecase.concert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.concertDate.ConcertDateRepository;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatRepository;
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
		Concert concert = getConcertEntity(concertId);

		List<ConcertDate> availableDates = concertDateRepository.findAvailableDates(concert.id());
		if (availableDates.isEmpty()) {
			log.debug("콘서트 예약 가능 날짜 조회 - 없음: CONCERT_ID - {}", concert.id());
			return Collections.emptyList();
		}

		List<ConcertDate> concertDates = new ArrayList<>();
		for (ConcertDate availableDate : availableDates) {
			Integer availableSeatCount = seatRepository.countRemainingSeat(availableDate.id());
			if (availableSeatCount <= 0) {
				log.debug("콘서트 예약 가능 날짜 조회 - 좌석 없음: CONCERT_DATE_ID - {}", availableDate.id());
				continue;
			}

			concertDates.add(availableDate.withRemainingSeatCount(availableSeatCount));
		}

		return concertDates;
	}

	public List<Seat> getAvailableSeats(UUID concertId, UUID concertDateId) throws CustomException {
		Concert concert = getConcertEntity(concertId);

		ConcertDate availableDate = concertDateRepository.findAvailableDate(concert.id(), concertDateId)
			.orElseThrow(() -> {
				log.warn("콘서트 예약 가능 좌석 조회 실패: CONCERT_ID - {}, CONCERT_DATE_ID - {}", concert.id(), concertDateId);
				return new CustomException(ErrorCode.CANNOT_RESERVATION_DATE);
			});

		List<Seat> availableSeats = seatRepository.findAvailableSeats(availableDate.id());
		if (availableSeats.isEmpty()) {
			log.debug("콘서트 예약 가능 좌석 조회 - 없음: CONCERT_DATE_ID - {}", availableDate.id());
			return Collections.emptyList();
		}

		return availableSeats;
	}

	private Concert getConcertEntity(UUID concertId) throws CustomException {
		try {
			Concert concert = concertRepository.findById(concertId)
				.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

			log.debug("콘서트 조회: CONCERT_ID - {}", concertId);
			return concert;
		} catch (CustomException e) {
			log.warn("콘서트 조회 실패: CONCERT_ID - {}", concertId);
			throw e;
		}
	}
}
