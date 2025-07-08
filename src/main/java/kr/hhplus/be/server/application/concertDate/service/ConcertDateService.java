package kr.hhplus.be.server.application.concertDate.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.concert.port.in.ExistsConcertInput;
import kr.hhplus.be.server.application.concertDate.port.out.ConcertDateRepository;
import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConcertDateService {

	private final ConcertDateRepository concertDateRepository;
	private final ExistsConcertInput existsConcertInput;

	public void existsConcertDate(UUID concertDateId) throws CustomException {
		if (!concertDateRepository.existsById(concertDateId)) {
			log.warn("콘서트 예약 가능 좌석 조회 실패: CONCERT_DATE_ID - {}", concertDateId);
			throw new CustomException(ErrorCode.CANNOT_RESERVATION_DATE);
		}
	}

	public List<ConcertDate> getAvailableConcertDates(UUID concertId) throws CustomException {
		existsConcertInput.existsConcert(concertId);
		return concertDateRepository.findAvailableDatesWithAvailableSeatCount(concertId).concertDates();
	}

	public void validDeadLine(UUID concertDateId) throws CustomException {
		ConcertDate concertDate = getConcertDate(concertDateId);
		if (!concertDate.checkDeadline())
			throw new CustomException(ErrorCode.OVER_DEADLINE);
	}

	public ConcertDate getConcertDate(UUID concertDateId) throws CustomException {
		return concertDateRepository.findById(concertDateId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_DATE_NOT_FOUND));
	}
}
