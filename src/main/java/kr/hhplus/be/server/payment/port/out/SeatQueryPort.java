package kr.hhplus.be.server.payment.port.out;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.Seat;

public interface SeatQueryPort {
    Seat paidSeat(UUID seatId, UUID tokenId) throws CustomException;
}
