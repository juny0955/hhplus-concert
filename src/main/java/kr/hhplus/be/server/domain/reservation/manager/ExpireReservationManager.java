package kr.hhplus.be.server.domain.reservation.manager;

import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.reservation.domain.ReservationExpiredEvent;
import kr.hhplus.be.server.domain.reservation.port.out.CancelPaymentPort;
import kr.hhplus.be.server.domain.reservation.port.out.CheckHoldSeatPort;
import kr.hhplus.be.server.domain.reservation.port.out.ExpireSeatPort;
import kr.hhplus.be.server.domain.reservation.port.out.SaveReservationPort;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ExpireReservationManager {

    private final CheckHoldSeatPort checkHoldSeatPort;
    private final SaveReservationPort saveReservationPort;
    private final CancelPaymentPort cancelPaymentPort;
    private final ExpireSeatPort expireSeatPort;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @DistributedLock(key = "reservation:#pendingReservation.id()")
    public void process(Reservation pendingReservation) throws CustomException {
        if (checkHoldSeatPort.checkHoldSeat(pendingReservation.seatId()))
            return;

        Seat seat = expireSeatPort.expireSeat(pendingReservation.seatId());
        Reservation reservation = saveReservationPort.saveReservation(pendingReservation.expired());
        Payment payment = cancelPaymentPort.cancelPayment(pendingReservation.id());

        eventPublisher.publishEvent(ReservationExpiredEvent.from(reservation, payment, seat));
    }
}
