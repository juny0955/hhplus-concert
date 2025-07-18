package kr.hhplus.be.server.reservation.usecase.reservation;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.PaidReservationFailEvent;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.reservation.domain.PaidReservationEvent;
import kr.hhplus.be.server.reservation.domain.PaidUserFailEvent;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.domain.ReservationCreatedEvent;
import kr.hhplus.be.server.reservation.port.in.reservation.CreateReservationUseCase;
import kr.hhplus.be.server.reservation.port.in.reservation.ExpireReservationUseCase;
import kr.hhplus.be.server.reservation.port.in.reservation.PaidReservationUseCase;
import kr.hhplus.be.server.reservation.port.in.reservation.ReserveSeatCommand;
import kr.hhplus.be.server.reservation.port.out.PaymentQueryPort;
import kr.hhplus.be.server.reservation.port.out.QueueTokenQueryPort;
import kr.hhplus.be.server.reservation.port.out.ReservationEventPublishPort;
import kr.hhplus.be.server.reservation.port.out.SeatQueryPort;
import kr.hhplus.be.server.reservation.port.out.reservation.GetReservationPort;
import kr.hhplus.be.server.reservation.port.out.reservation.SaveReservationPort;
import kr.hhplus.be.server.reservation.port.out.seathold.CheckHoldSeatPort;
import kr.hhplus.be.server.reservation.port.out.seathold.HoldSeatPort;
import kr.hhplus.be.server.reservation.port.out.seathold.ReleaseSeatHoldPort;
import kr.hhplus.be.server.reservation.usecase.reservation.manager.ReservationTransactionManager;
import kr.hhplus.be.server.user.domain.PaidUserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService implements
        CreateReservationUseCase,
        ExpireReservationUseCase,
        PaidReservationUseCase {

    private final ApplicationEventPublisher eventPublisher;
    private final QueueTokenQueryPort queueTokenQueryPort;
    private final SeatQueryPort seatQueryPort;
    private final PaymentQueryPort paymentQueryPort;
    private final HoldSeatPort holdSeatPort;
    private final ReleaseSeatHoldPort releaseSeatHoldPort;
    private final CheckHoldSeatPort checkHoldSeatPort;
    private final GetReservationPort getReservationPort;
    private final SaveReservationPort saveReservationPort;
    private final ReservationTransactionManager reservationTransactionManager;
    private final ReservationEventPublishPort reservationEventPublishPort;

    @DistributedLock(key = "reserve:concert:#command.seatId()")
    @Transactional
    @Override
    public Reservation createReservation(ReserveSeatCommand command) throws Exception {
        QueueToken queueToken = queueTokenQueryPort.getActiveToken(command.queueTokenId());
        checkHoldSeatPort.checkHoldSeat(command.seatId());

        Reservation reservation = saveReservationPort.saveReservation(Reservation.of(queueToken.userId(), command.seatId()));
        Seat seat = seatQueryPort.reserveSeat(command.seatId(), command.concertId(), command.concertId());
        Payment payment = paymentQueryPort.createPayment(queueToken.userId(), reservation.id(), seat.price());

        holdSeatPort.holdSeat(seat.id(), queueToken.userId());

        eventPublisher.publishEvent(ReservationCreatedEvent.from(reservation, payment, seat, queueToken.userId(), command.concertId()));
        return reservation;
    }

    @DistributedLock(key = "reservation:#reservationId")
    @Transactional
    @Override
    public void paidReservation(PaidUserEvent event) {
        try {
            Reservation reservation = getReservationPort.getReservation(event.reservationId());
            Reservation paidReservation = saveReservationPort.saveReservation(reservation.paid());
            releaseSeatHoldPort.releaseSeatHold(paidReservation.seatId());

            reservationEventPublishPort.publishPaidReservationEvent(PaidReservationEvent.from(event));
        } catch (Exception e) {
            reservationEventPublishPort.publishPaidUserFailEvent(PaidUserFailEvent.of(event, e.getMessage()));
        }
    }

    @DistributedLock(key = "reservation:#reservationId")
    @Transactional
    @Override
    public void paidReservationFail(PaidReservationFailEvent event) throws CustomException {
        Reservation reservation = getReservationPort.getReservation(event.reservationId());
        saveReservationPort.saveReservation(reservation.paidFail());

        reservationEventPublishPort.publishPaidUserFailEvent(PaidUserFailEvent.from(event));
    }

    @Override
    public void expireReservation() {
        List<Reservation> reservations = getReservationPort.getPendingReservations();

        for (Reservation pendingReservation : reservations) {
            try {
                checkHoldSeatPort.checkHoldSeat(pendingReservation.seatId());

                reservationTransactionManager.process(pendingReservation);
            } catch (Exception e) {
                log.warn("예약 만료 처리 스케줄러 처리 중 예외발생", e);
            }
        }
    }
}
