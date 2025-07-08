package kr.hhplus.be.server.application.concert.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.concert.service.ConcertService;
import kr.hhplus.be.server.application.concert.port.in.ValidOpenConcertUseCase;
import kr.hhplus.be.server.exception.CustomException;
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
