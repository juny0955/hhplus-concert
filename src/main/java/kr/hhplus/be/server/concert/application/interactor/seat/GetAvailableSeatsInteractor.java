package kr.hhplus.be.server.concert.application.interactor.seat;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.application.service.SeatApplicationService;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.ports.in.seat.GetAvailableSeatsInput;
import kr.hhplus.be.server.framework.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetAvailableSeatsInteractor implements GetAvailableSeatsInput {

	private final SeatApplicationService seatApplicationService;

	@Override
	public List<Seat> getAvailableSeats(UUID concertId, UUID concertDateId) throws CustomException {
		return seatApplicationService.getAvailableSeats(concertId, concertDateId);
	}
}
