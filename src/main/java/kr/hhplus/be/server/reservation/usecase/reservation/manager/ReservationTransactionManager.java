package kr.hhplus.be.server.reservation.usecase.reservation.manager;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.domain.ReservationExpiredEvent;
import kr.hhplus.be.server.reservation.port.out.PaymentQueryPort;
import kr.hhplus.be.server.reservation.port.out.SeatQueryPort;
import kr.hhplus.be.server.reservation.port.out.reservation.SaveReservationPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationTransactionManager {

    private final SaveReservationPort saveReservationPort;
    private final PaymentQueryPort paymentQueryPort;
    private final SeatQueryPort seatQueryPort;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @DistributedLock(key = "'reservation:'+#pendingReservation.id()")
    public void process(Reservation pendingReservation) throws CustomException {
        Seat seat = seatQueryPort.expireSeat(pendingReservation.seatId());
        Reservation reservation = saveReservationPort.saveReservation(pendingReservation.expired());
        Payment payment = paymentQueryPort.cancelPayment(pendingReservation.id());

        eventPublisher.publishEvent(ReservationExpiredEvent.from(reservation, payment, seat));
    }
}
