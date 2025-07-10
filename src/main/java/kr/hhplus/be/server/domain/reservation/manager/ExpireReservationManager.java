package kr.hhplus.be.server.domain.reservation.manager;

import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.reservation.domain.ReservationExpiredEvent;
import kr.hhplus.be.server.domain.reservation.port.out.*;
import kr.hhplus.be.server.domain.concert.domain.seat.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ExpireReservationManager {

    private final SeatHoldQueryPort seatHoldQueryPort;
    private final SaveReservationPort saveReservationPort;
    private final PaymentQueryPort paymentQueryPort;
    private final SeatQueryPort seatQueryPort;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @DistributedLock(key = "reservation:#pendingReservation.id()")
    public void process(Reservation pendingReservation) throws CustomException {
        if (seatHoldQueryPort.checkHoldSeat(pendingReservation.seatId()))
            return;

        Seat seat = seatQueryPort.expireSeat(pendingReservation.seatId());
        Reservation reservation = saveReservationPort.saveReservation(pendingReservation.expired());
        Payment payment = paymentQueryPort.cancelPayment(pendingReservation.id());

        eventPublisher.publishEvent(ReservationExpiredEvent.from(reservation, payment, seat));
    }
}
