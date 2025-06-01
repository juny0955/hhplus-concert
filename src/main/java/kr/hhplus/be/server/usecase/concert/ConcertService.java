package kr.hhplus.be.server.usecase.concert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.entity.concert.ConcertDate;
import kr.hhplus.be.server.entity.concert.Seat;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.ConcertDateEntity;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.ConcertEntity;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.SeatEntity;
import kr.hhplus.be.server.usecase.exception.CustomException;
import kr.hhplus.be.server.usecase.exception.ErrorCode;
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
		ConcertEntity concertEntity = concertRepository.findById(concertId.toString())
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

		List<ConcertDateEntity> availableDates = concertDateRepository.findAvailableDates(concertEntity.getId());
		if (availableDates.isEmpty())
			return Collections.emptyList();

		List<ConcertDate> concertDates = new ArrayList<>();
		for (ConcertDateEntity availableDate : availableDates) {
			Integer availableSeatCount = seatRepository.countRemainingSeat(availableDate.getId());
			concertDates.add(availableDate.toDomain(availableSeatCount));
		}

		return concertDates;
	}

	public List<Seat> getAvailableSeats(UUID concertId, UUID concertDateId) throws CustomException {
		ConcertEntity concertEntity = concertRepository.findById(concertId.toString())
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

		ConcertDateEntity availableDate = concertDateRepository.findAvailableDate(concertEntity.getId(), concertDateId.toString())
			.orElseThrow(() -> new CustomException(ErrorCode.CANNOT_RESERVATION_DATE));

		List<SeatEntity> availableSeats = seatRepository.findAvailableSeats(availableDate.getId());
		if (availableSeats.isEmpty())
			return Collections.emptyList();

		return availableSeats.stream()
			.map(SeatEntity::toDomain)
			.toList();
	}
}
