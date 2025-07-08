package kr.hhplus.be.server.application.concert.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.concert.port.in.ExistsConcertInput;
import kr.hhplus.be.server.application.concert.port.out.ExistsConcertPort;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExistsConcertInteractor implements ExistsConcertInput {


	private final ExistsConcertPort existsConcertPort;

	@Override
	public void existsConcert(UUID concertId) throws CustomException {
		existsConcertPort.existsConcert(concertId);
	}
}
