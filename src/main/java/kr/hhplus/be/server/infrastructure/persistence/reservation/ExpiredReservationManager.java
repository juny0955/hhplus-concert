package kr.hhplus.be.server.infrastructure.persistence.reservation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentRepository;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.domain.ReservationDomainResult;
import kr.hhplus.be.server.reservation.domain.ReservationDomainService;
import kr.hhplus.be.server.reservation.domain.ReservationRepository;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.SeatHoldRepository;
import kr.hhplus.be.server.concert.domain.seat.SeatRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpiredReservationManager {

    private final ReservationRepository reservationRepository;
    private final SeatHoldRepository seatHoldRepository;
    private final SeatRepository seatRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationDomainService reservationDomainService;

    public List<Reservation> getPendingReservations() {
        return reservationRepository.findByStatusPending();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ExpiredReservationResult processExpiredReservation(Reservation reservation) throws CustomException {
        if (!seatHoldRepository.isHoldSeat(reservation.seatId(), reservation.userId()))
            return null;

        Seat seat 	= getSeatById(reservation.seatId());
        Payment payment = getPaymentByReservationId(reservation.id());

        ReservationDomainResult result = reservationDomainService.processReservationExpired(reservation, payment, seat);

        Seat 		updatedSeat 		= seatRepository.save(result.seat());
        Reservation updatedReservation 	= reservationRepository.save(result.reservation());
        Payment 	updatedPayment 		= paymentRepository.save(payment);

        return ExpiredReservationResult.from(updatedReservation.id(), updatedPayment.id(), updatedSeat.id(), updatedReservation.userId());
    }

    private Seat getSeatById(UUID seatId) throws CustomException {
        return seatRepository.findById(seatId)
            .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
    }

    private Payment getPaymentByReservationId(UUID reservationId) throws CustomException {
        return paymentRepository.findByReservationId(reservationId)
            .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
    }
}
