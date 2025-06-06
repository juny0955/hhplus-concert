package kr.hhplus.be.server.usecase.concert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.ConcertDate;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.ConcertDateEntity;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.JpaConcertDateRepository;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.ConcertEntity;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.JpaConcertRepository;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.SeatEntity;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.JpaSeatRepository;
import kr.hhplus.be.server.usecase.exception.CustomException;
import kr.hhplus.be.server.usecase.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ConcertService {

	private final JpaConcertRepository jpaConcertRepository;
	private final JpaConcertDateRepository jpaConcertDateRepository;
	private final JpaSeatRepository jpaSeatRepository;

	public List<ConcertDate> getAvailableConcertDates(UUID concertId) throws CustomException {
		ConcertEntity concertEntity = getConcertEntity(concertId);

		List<ConcertDateEntity> availableDates = jpaConcertDateRepository.findAvailableDates(concertEntity.getId());
		if (availableDates.isEmpty()) {
			log.debug("콘서트 예약 가능 날짜 조회 - 없음: CONCERT_ID - {}", concertEntity.getId());
			return Collections.emptyList();
		}

		List<ConcertDate> concertDates = new ArrayList<>();
		for (ConcertDateEntity availableDate : availableDates) {
			Integer availableSeatCount = jpaSeatRepository.countRemainingSeat(availableDate.getId());
			if (availableSeatCount <= 0) {
				log.debug("콘서트 예약 가능 날짜 조회 - 좌석 없음: CONCERT_DATE_ID - {}", availableDate.getId());
				continue;
			}

			concertDates.add(availableDate.toDomain(availableSeatCount));
		}

		return concertDates;
	}

	public List<Seat> getAvailableSeats(UUID concertId, UUID concertDateId) throws CustomException {
		ConcertEntity concertEntity = getConcertEntity(concertId);

		ConcertDateEntity availableDate = jpaConcertDateRepository.findAvailableDate(concertEntity.getId(), concertDateId.toString())
			.orElseThrow(() -> {
				log.warn("콘서트 예약 가능 좌석 조회 실패: CONCERT_ID - {}, CONCERT_DATE_ID - {}", concertEntity.getId(), concertDateId);
				return new CustomException(ErrorCode.CANNOT_RESERVATION_DATE);
			});

		List<SeatEntity> availableSeats = jpaSeatRepository.findAvailableSeats(availableDate.getId());
		if (availableSeats.isEmpty()) {
			log.debug("콘서트 예약 가능 좌석 조회 - 없음: CONCERT_DATE_ID - {}", availableDate.getId());
			return Collections.emptyList();
		}

		return availableSeats.stream()
			.map(SeatEntity::toDomain)
			.toList();
	}

	private ConcertEntity getConcertEntity(UUID concertId) throws CustomException {
		try {
			ConcertEntity concertEntity = jpaConcertRepository.findById(concertId.toString())
				.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

			log.debug("콘서트 조회: CONCERT_ID - {}", concertId);
			return concertEntity;
		} catch (CustomException e) {
			log.warn("콘서트 조회 실패: CONCERT_ID - {}", concertId);
			throw e;
		}
	}
}
