package kr.hhplus.be.server.domain.payment.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.domain.seat.Seat;

import java.util.UUID;

public interface SeatQueryPort {
    Seat paidSeat(UUID seatId) throws CustomException;
}
