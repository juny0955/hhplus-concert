package kr.hhplus.be.server.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatClass;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import kr.hhplus.be.server.domain.user.User;

public class TestDataFactory {
	public static Concert createConcert() {
		return Concert.builder()
			.title("GD 콘서트")
			.artist("GD")
			.build();
	}

	public static ConcertDate createConcertDate(UUID concertId) {
		return ConcertDate.builder()
			.concertId(concertId)
			.date(LocalDateTime.now().plusDays(7))
			.deadline(LocalDateTime.now().plusDays(5))
			.build();
	}

	public static Seat createSeat(UUID concertDateId) {
		return Seat.builder()
			.concertDateId(concertDateId)
			.seatNo(1)
			.price(BigDecimal.valueOf(50000))
			.seatClass(SeatClass.VIP)
			.status(SeatStatus.AVAILABLE)
			.build();
	}

	public static User createUser() {
		return User.builder()
			.amount(BigDecimal.valueOf(100000))
			.build();
	}

	public static User createPoorUser() {
		return User.builder()
			.amount(BigDecimal.valueOf(10000)) // 좌석 가격(50000)보다 적은 금액
			.build();
	}

	public static Reservation createReservation(UUID userId, UUID seatId) {
		return Reservation.builder()
			.userId(userId)
			.seatId(seatId)
			.status(ReservationStatus.PENDING)
			.build();
	}

	public static Payment createPayment(UUID userId, UUID reservationId) {
		return Payment.builder()
			.userId(userId)
			.reservationId(reservationId)
			.amount(BigDecimal.valueOf(50000))
			.status(PaymentStatus.PENDING)
			.build();
	}

}
