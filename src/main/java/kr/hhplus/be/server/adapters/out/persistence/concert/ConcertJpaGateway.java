package kr.hhplus.be.server.adapters.out.persistence.concert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.concert.port.out.ConcertRepository;
import kr.hhplus.be.server.application.concert.port.out.GetConcertPort;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConcertJpaGateway implements ConcertRepository, GetConcertPort {

	private final JpaConcertRepository jpaConcertRepository;

	@Override
	public Concert getConcert(UUID concertId) throws CustomException {
		return jpaConcertRepository.findById(concertId.toString())
			.map(ConcertEntity::toDomain)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));
	}

	@Override
	public void existsConcert(UUID concertId) throws CustomException {
		if (!jpaConcertRepository.existsById(concertId.toString()))
			throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);
	}

	@Override
	public Concert save(Concert concert) {
		ConcertEntity concertEntity = jpaConcertRepository.save(ConcertEntity.from(concert));
		return concertEntity.toDomain();
	}

	@Override
	public Optional<Concert> findById(UUID concertId) {
		return jpaConcertRepository.findById(concertId.toString())
			.map(ConcertEntity::toDomain);
	}

	@Override
	public boolean existsById(UUID concertId) {
		return jpaConcertRepository.existsById(concertId.toString());
	}

	@Override
	public void deleteAll() {
		jpaConcertRepository.deleteAll();
	}

	@Override
	public List<Concert> findByOpenConcerts() {
		return jpaConcertRepository.findByOpenConcerts().stream()
			.map(ConcertEntity::toDomain)
			.toList();
	}
}
