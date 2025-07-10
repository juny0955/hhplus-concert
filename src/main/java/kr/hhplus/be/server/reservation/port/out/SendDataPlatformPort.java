package kr.hhplus.be.server.reservation.port.out;

import kr.hhplus.be.server.reservation.domain.ReservationPayload;

public interface SendDataPlatformPort {
    void send(ReservationPayload reservationPayload);
}
