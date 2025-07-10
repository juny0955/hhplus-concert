package kr.hhplus.be.server.reservation.usecase.reservation;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.queue.QueueToken;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.domain.ReservationCreatedEvent;
import kr.hhplus.be.server.reservation.domain.ReservationPayload;
import kr.hhplus.be.server.reservation.port.in.reservation.CreateReservationUseCase;
import kr.hhplus.be.server.reservation.port.in.reservation.ExpireReservationUseCase;
import kr.hhplus.be.server.reservation.port.in.reservation.PaidReservationUseCase;
import kr.hhplus.be.server.reservation.port.in.reservation.ReserveSeatCommand;
import kr.hhplus.be.server.reservation.port.out.ConcertQueryPort;
import kr.hhplus.be.server.reservation.port.out.PaymentQueryPort;
import kr.hhplus.be.server.reservation.port.out.QueueTokenQueryPort;
import kr.hhplus.be.server.reservation.port.out.SeatQueryPort;
import kr.hhplus.be.server.reservation.port.out.SendDataPlatformPort;
import kr.hhplus.be.server.reservation.port.out.reservation.GetReservationPort;
import kr.hhplus.be.server.reservation.port.out.reservation.SaveReservationPort;
import kr.hhplus.be.server.reservation.port.out.seathold.CheckHoldSeatPort;
import kr.hhplus.be.server.reservation.port.out.seathold.HoldSeatPort;
import kr.hhplus.be.server.reservation.usecase.reservation.manager.ReservationTransactionManager;
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
    private final ConcertQueryPort concertQueryPort;
    private final SeatQueryPort seatQueryPort;
    private final PaymentQueryPort paymentQueryPort;
    private final HoldSeatPort holdSeatPort;
    private final CheckHoldSeatPort checkHoldSeatPort;
    private final GetReservationPort getReservationPort;
    private final SaveReservationPort saveReservationPort;
    private final SendDataPlatformPort sendDataPlatformPort;
    private final ReservationTransactionManager reservationTransactionManager;

    @Override
    @DistributedLock(key = "reserve:concert:#command.seatId()")
    @Transactional
    public Reservation createReservation(ReserveSeatCommand command) throws Exception {
        QueueToken queueToken = queueTokenQueryPort.getActiveToken(command.queueTokenId());
        checkHoldSeatPort.checkHoldSeat(command.seatId());
        concertQueryPort.validOpenConcert(command.concertId());
        concertQueryPort.validDeadLine(command.concertDateId());

        Seat seat = seatQueryPort.reserveSeat(command.seatId());
        Reservation reservation = saveReservationPort.saveReservation(Reservation.of(queueToken.userId(), seat.id()));
        Payment payment = paymentQueryPort.createPayment(queueToken.userId(), reservation.id(), seat.price());

        holdSeatPort.holdSeat(seat.id(), queueToken.userId());

        eventPublisher.publishEvent(ReservationCreatedEvent.from(reservation, payment, seat, queueToken.userId(), command.concertId()));
        return reservation;
    }

    @Override
    @DistributedLock(key = "reservation:#reservationId")
    @Transactional
    public Reservation paidReservation(UUID reservationId) throws CustomException {
        Reservation reservation = getReservationPort.getReservation(reservationId);
        return saveReservationPort.saveReservation(reservation.paid());
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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void sendDataPlatform(ReservationCreatedEvent event) {
        try {
            sendDataPlatformPort.send(ReservationPayload.from(event));
        } catch (Exception e) {
            log.warn("데이터 플랫폼 전송 실패");
        }
    }
}
