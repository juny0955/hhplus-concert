package kr.hhplus.be.server.domain.reservation.adapter.out.concert;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concert.port.in.ValidOpenConcertUseCase;
import kr.hhplus.be.server.domain.reservation.port.out.ValidOpenConcertPort;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConcertAdapter implements ValidOpenConcertPort {

	private final ValidOpenConcertUseCase validOpenConcertUseCase;

	@Override
	public void validOpenConcert(UUID concertId) throws CustomException {
		validOpenConcertUseCase.validOpenConcert(concertId);
	}
}
