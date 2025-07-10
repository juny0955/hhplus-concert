package kr.hhplus.be.server.reservation.port.out;

import kr.hhplus.be.server.reservation.domain.reservation.ReservationPayload;

public interface SendDataPlatformPort {
    void send(ReservationPayload reservationPayload);
}
