package kr.hhplus.be.server.reservation.adapter.out.internal.concert;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.port.in.concert.ExistsConcertUseCase;
import kr.hhplus.be.server.reservation.port.out.ConcertQueryPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConcertAdapter implements ConcertQueryPort {

	private final ExistsConcertUseCase existsConcertUseCase;

	@Override
	public void existsConcert(UUID concertId) throws CustomException {
		existsConcertUseCase.existsConcert(concertId);
	}
}
