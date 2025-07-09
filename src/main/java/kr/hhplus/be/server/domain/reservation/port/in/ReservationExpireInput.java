package kr.hhplus.be.server.domain.reservation.port.in;

public interface ReservationExpireInput {
    void expiredReservation() throws Exception;
}
