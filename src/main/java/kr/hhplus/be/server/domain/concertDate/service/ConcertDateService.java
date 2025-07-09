package kr.hhplus.be.server.domain.concertDate.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concertDate.port.out.ConcertDateRepository;
import kr.hhplus.be.server.domain.concertDate.domain.ConcertDate;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConcertDateService {

	private final ConcertDateRepository concertDateRepository;

	public void existsConcertDate(UUID concertDateId) throws CustomException {
		if (!concertDateRepository.existsById(concertDateId)) {
			log.warn("콘서트 예약 가능 좌석 조회 실패: CONCERT_DATE_ID - {}", concertDateId);
			throw new CustomException(ErrorCode.CANNOT_RESERVATION_DATE);
		}
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
