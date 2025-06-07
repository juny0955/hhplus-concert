package kr.hhplus.be.server.interfaces.gateway.repository.concertDate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concert.ConcertDate;
import kr.hhplus.be.server.usecase.concert.ConcertDateRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConcertDateJpaGateway implements ConcertDateRepository {

	private final JpaConcertDateRepository jpaConcertDateRepository;

	@Override
	public Optional<ConcertDate> findById(UUID concertDateId) {
		return jpaConcertDateRepository.findById(concertDateId.toString())
			.map(ConcertDateEntity::toDomain);
	}

	@Override
	public List<ConcertDate> findAvailableDates(UUID concertId) {
		return jpaConcertDateRepository.findAvailableDates(concertId.toString()).stream()
			.map(ConcertDateEntity::toDomain)
			.toList();
	}

	@Override
	public Optional<ConcertDate> findAvailableDate(UUID concertId, UUID concertDateId) {
		return jpaConcertDateRepository.findAvailableDate(concertId.toString(), concertDateId.toString())
			.map(ConcertDateEntity::toDomain);
	}
}
