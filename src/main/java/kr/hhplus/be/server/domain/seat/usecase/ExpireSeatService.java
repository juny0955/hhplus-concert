package kr.hhplus.be.server.domain.seat.usecase;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.seat.port.in.ExpireSeatUseCase;
import kr.hhplus.be.server.domain.seat.port.out.GetSeatPort;
import kr.hhplus.be.server.domain.seat.port.out.SaveSeatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExpireSeatService implements ExpireSeatUseCase {

	private final GetSeatPort getSeatPort;
	private final SaveSeatPort saveSeatPort;

	@Override
	@Transactional
	public Seat expireSeat(UUID seatId) throws CustomException {
		Seat seat = getSeatPort.getSeat(seatId);
		return saveSeatPort.saveSeat(seat.expire());
	}
}
