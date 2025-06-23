package kr.hhplus.be.server.usecase.reservation.input;

import kr.hhplus.be.server.framework.exception.CustomException;

public interface ReservationExpiredInput {
    void expiredReservation() throws CustomException;
}
