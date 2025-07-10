package kr.hhplus.be.server.concert.adapter.out.persistence.concert;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.concert.port.out.concert.GetConcertPort;
import kr.hhplus.be.server.concert.port.out.concert.SaveConcertPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConcertJpaAdapter implements GetConcertPort, SaveConcertPort {

	private final JpaConcertRepository jpaConcertRepository;

	@Override
	public Concert getConcert(UUID concertId) throws CustomException {
		return jpaConcertRepository.findById(concertId.toString())
			.map(ConcertEntity::toDomain)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));
	}

	@Override
	public List<Concert> getOpenConcerts() {
		return jpaConcertRepository.findByOpenConcerts().stream()
			.map(ConcertEntity::toDomain)
			.toList();
	}

	@Override
	public void existsConcert(UUID concertId) throws CustomException {
		if (!jpaConcertRepository.existsById(concertId.toString()))
			throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);
	}

	@Override
	public Concert saveConcert(Concert concert) {
		ConcertEntity concertEntity = jpaConcertRepository.save(ConcertEntity.from(concert));
		return concertEntity.toDomain();
	}

	@Override
	public Concert getConcertByConcertDateId(UUID concertDateId) throws CustomException {
		return jpaConcertRepository.findByConcertDateId(concertDateId.toString())
			.map(ConcertEntity::toDomain)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));
	}
}
