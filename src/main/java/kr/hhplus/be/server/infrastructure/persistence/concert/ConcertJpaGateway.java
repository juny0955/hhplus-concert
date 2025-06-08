package kr.hhplus.be.server.infrastructure.persistence.concert;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConcertJpaGateway implements ConcertRepository {

	private final JpaConcertRepository jpaConcertRepository;

	@Override
	public boolean existsById(UUID concertId) {
		return jpaConcertRepository.existsById(concertId.toString());
	}

	@Override
	public Optional<Concert> findById(UUID concertId) {
		return jpaConcertRepository.findById(concertId.toString())
			.map(ConcertEntity::toDomain);
	}
}
