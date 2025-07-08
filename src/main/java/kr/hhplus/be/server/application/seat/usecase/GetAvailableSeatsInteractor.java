package kr.hhplus.be.server.application.seat.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.seat.port.in.GetAvailableSeatsInput;
import kr.hhplus.be.server.application.seat.service.SeatService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetAvailableSeatsInteractor implements GetAvailableSeatsInput {

	private final SeatService seatService;

	@Override
	public List<Seat> getAvailableSeats(UUID concertId, UUID concertDateId) throws CustomException {
		return seatService.getAvailableSeats(concertId, concertDateId);
	}
}
