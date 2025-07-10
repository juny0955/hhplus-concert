package kr.hhplus.be.server.domain.reservation.adapter.out.internal.concert;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.port.in.concert.ValidOpenConcertUseCase;
import kr.hhplus.be.server.domain.concert.port.in.concertDate.ValidDeadLineUseCase;
import kr.hhplus.be.server.domain.reservation.port.out.ConcertQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConcertAdapter implements ConcertQueryPort {

	private final ValidOpenConcertUseCase validOpenConcertUseCase;
	private final ValidDeadLineUseCase validDeadLineUseCase;

	@Override
	public void validOpenConcert(UUID concertId) throws CustomException {
		validOpenConcertUseCase.validOpenConcert(concertId);
	}

	@Override
	public void validDeadLine(UUID concertDateId) throws CustomException {
		validDeadLineUseCase.validDeadLine(concertDateId);
	}
}
