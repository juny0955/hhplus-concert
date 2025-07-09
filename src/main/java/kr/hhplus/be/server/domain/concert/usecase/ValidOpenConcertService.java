package kr.hhplus.be.server.domain.concert.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.concert.port.in.ValidOpenConcertUseCase;
import kr.hhplus.be.server.domain.concert.port.out.GetConcertPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidOpenConcertService implements ValidOpenConcertUseCase {

	private final GetConcertPort getConcertPort;

	@Override
	public void validOpenConcert(UUID concertId) throws CustomException {
		Concert concert = getConcertPort.getConcert(concertId);
		if (!concert.isOpen())
			throw new CustomException(ErrorCode.CONCERT_NOT_OPEN);
	}
}
