package kr.hhplus.be.server.usecase.reservation;

import java.util.UUID;

public interface SeatHoldRepository {
	void hold(UUID seatId);
}
