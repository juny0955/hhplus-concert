package kr.hhplus.be.server.domain.reservation.port.out;

import kr.hhplus.be.server.domain.reservation.domain.ReservationPayload;

public interface SendDataPlatformPort {
    void send(ReservationPayload reservationPayload);
}
