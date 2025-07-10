package kr.hhplus.be.server.reservation.adapter.out.internal.concert;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.port.in.concert.ExistsConcertUseCase;
import kr.hhplus.be.server.concert.port.in.concert.ValidOpenConcertUseCase;
import kr.hhplus.be.server.concert.port.in.concertDate.ValidDeadLineUseCase;
import kr.hhplus.be.server.reservation.port.out.ConcertQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConcertAdapter implements ConcertQueryPort {

	private final ValidOpenConcertUseCase validOpenConcertUseCase;
	private final ValidDeadLineUseCase validDeadLineUseCase;
	private final ExistsConcertUseCase existsConcertUseCase;

	@Override
	public void validOpenConcert(UUID concertId) throws CustomException {
		validOpenConcertUseCase.validOpenConcert(concertId);
	}

	@Override
	public void validDeadLine(UUID concertDateId) throws CustomException {
		validDeadLineUseCase.validDeadLine(concertDateId);
	}

	@Override
	public void existsConcert(UUID concertId) throws CustomException {
		existsConcertUseCase.existsConcert(concertId);
	}
}
