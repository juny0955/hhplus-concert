package kr.hhplus.be.server.domain.concert.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.concert.port.in.ValidOpenConcertUseCase;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidOpenConcertInteractor implements ValidOpenConcertUseCase {

	private final ConcertService concertService;

	@Override
	public void validOpenConcert(UUID concertId) throws CustomException {
		concertService.validOpenConcert(concertId);
	}
}
