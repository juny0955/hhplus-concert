package kr.hhplus.be.server.usecase.concert;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.entity.concert.ConcertDate;
import kr.hhplus.be.server.entity.concert.Seat;
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

	public List<ConcertDate> getAvailableConcertDates(UUID concertId) {

		return null;
	}

	public List<Seat> getAvailableSeats(UUID concertId, UUID concertDateId) {

		return null;
	}
}
