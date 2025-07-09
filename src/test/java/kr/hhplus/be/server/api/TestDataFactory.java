package kr.hhplus.be.server.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.concertDate.domain.ConcertDate;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.payment.domain.PaymentStatus;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.reservation.domain.ReservationStatus;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.seat.domain.SeatClass;
import kr.hhplus.be.server.domain.seat.domain.SeatStatus;
import kr.hhplus.be.server.domain.user.domain.User;

public class TestDataFactory {

	public static final BigDecimal INIT_USER_POINT = BigDecimal.valueOf(100_000);
	public static final BigDecimal INIT_POOR_USER_POINT = BigDecimal.valueOf(10_000);
	public static final BigDecimal INIT_SEAT_PRICE = BigDecimal.valueOf(50_000);

	public static Concert createConcert() {
		return Concert.builder()
			.title("GD 콘서트")
			.artist("GD")
			.openTime(LocalDateTime.now().minusDays(7))
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
			.price(INIT_SEAT_PRICE)
			.seatClass(SeatClass.VIP)
			.status(SeatStatus.AVAILABLE)
			.build();
	}

	public static User createUser() {
		return User.builder()
			.amount(INIT_USER_POINT)
			.build();
	}

	public static User createPoorUser() {
		return User.builder()
			.amount(INIT_POOR_USER_POINT) // 좌석 가격(50000)보다 적은 금액
			.build();
	}

	public static User createUserWithAmount(BigDecimal amount) {
		return User.builder()
			.amount(amount)
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
			.amount(INIT_SEAT_PRICE)
			.status(PaymentStatus.PENDING)
			.build();
	}

	public static Seat createSeatWithSeatNo(UUID concertDateId, int seatNo) {
		return Seat.builder()
			.concertDateId(concertDateId)
			.seatNo(seatNo)
			.price(INIT_SEAT_PRICE)
			.seatClass(SeatClass.VIP)
			.status(SeatStatus.AVAILABLE)
			.build();
	}

	public static Seat createReservedSeat(UUID concertDateId) {
		return Seat.builder()
			.concertDateId(concertDateId)
			.seatNo(1)
			.price(INIT_SEAT_PRICE)
			.seatClass(SeatClass.VIP)
			.status(SeatStatus.RESERVED)
			.build();
	}
}
