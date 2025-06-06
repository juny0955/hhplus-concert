package kr.hhplus.be.server.usecase.reservation;

import java.util.UUID;

public interface SeatLockRepository {
	boolean getLock(UUID seatId);
	void releaseLock(UUID seatId);
}
