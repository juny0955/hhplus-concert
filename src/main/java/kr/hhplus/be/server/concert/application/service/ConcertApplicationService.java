package kr.hhplus.be.server.concert.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.concert.ports.out.ConcertRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ConcertApplicationService {

	private final ConcertRepository concertRepository;

	public List<Concert> getOpenConcerts() {
		return concertRepository.findByOpenConcerts();
	}

	public void existsConcert(UUID concertId) throws CustomException {
		if (!concertRepository.existsById(concertId)) {
			log.warn("콘서트 조회 실패: CONCERT_ID - {}", concertId);
			throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);
		}

		log.debug("콘서트 조회: CONCERT_ID - {}", concertId);
	}

	public void validOpenConcert(UUID concertId) throws CustomException {
		Concert concert = getConcert(concertId);
		if (!concert.isOpen())
			throw new CustomException(ErrorCode.CONCERT_NOT_OPEN);
	}

	public Concert getConcert(UUID concertId) throws CustomException {
		return concertRepository.findById(concertId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));
	}
}
