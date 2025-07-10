package kr.hhplus.be.server.payment.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.Seat;

import java.util.UUID;

public interface SeatQueryPort {
    Seat paidSeat(UUID seatId) throws CustomException;
}
