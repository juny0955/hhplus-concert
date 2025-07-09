package kr.hhplus.be.server.domain.reservation.adapter.out.concertDate;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concertDate.port.in.ValidDeadLineUseCase;
import kr.hhplus.be.server.domain.reservation.port.out.ValidDeadLinePort;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConcertDateAdapter implements ValidDeadLinePort {

	private final ValidDeadLineUseCase validDeadLineUseCase;

	@Override
	public void validDeadLine(UUID concertDateId) throws CustomException {
		validDeadLineUseCase.validDeadLine(concertDateId);
	}
}
