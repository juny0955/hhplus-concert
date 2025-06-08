package kr.hhplus.be.server.domain.reservation;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatClass;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ReservationDomainServiceTest {

	@InjectMocks
	private ReservationDomainService reservationDomainService;

	private UUID userId;
	private UUID concertId;
	private UUID concertDateId;
	private UUID seatId;
	private ConcertDate concertDate;
	private Seat availableSeat;

	@BeforeEach
	void beforeEach() {
		LocalDateTime now = LocalDateTime.now();
		userId = UUID.randomUUID();
		concertId = UUID.randomUUID();
		concertDateId = UUID.randomUUID();
		seatId = UUID.randomUUID();

		concertDate = ConcertDate.builder()
			.id(concertDateId)
			.concertId(concertId)
			.date(now.plusDays(7))
			.deadline(now.plusDays(5))
			.createdAt(now)
			.updatedAt(now)
			.build();

		availableSeat = Seat.builder()
			.id(seatId)
			.concertDateId(concertDateId)
			.seatNo(10)
			.price(BigDecimal.valueOf(50000))
			.seatClass(SeatClass.VIP)
			.status(SeatStatus.AVAILABLE)
			.createdAt(now)
			.updatedAt(now)
			.build();
	}

	@Test
	@DisplayName("예약_처리_성공")
	void processReservation_Success() throws CustomException {
		ReservationDomainResult result = reservationDomainService.processReservation(concertDate, availableSeat, userId);

		assertThat(result).isNotNull();

		assertThat(result.seat().status()).isEqualTo(SeatStatus.RESERVED);
		assertThat(result.seat().id()).isEqualTo(seatId);
		assertThat(result.seat().price()).isEqualTo(BigDecimal.valueOf(50000));

		assertThat(result.reservation().userId()).isEqualTo(userId);
		assertThat(result.reservation().seatId()).isEqualTo(seatId);
		assertThat(result.reservation().status()).isEqualTo(ReservationStatus.PENDING);

		assertThat(result.payment().userId()).isEqualTo(userId);
		assertThat(result.payment().amount()).isEqualTo(BigDecimal.valueOf(50000));
		assertThat(result.payment().status()).isEqualTo(PaymentStatus.PENDING);
	}

	@Test
	@DisplayName("예약_처리_실패_좌석이_예약불가능한_상태")
	void processReservation_Failure_SeatNotAvailable() throws CustomException {
		Seat reservedSeat = Seat.builder()
			.id(seatId)
			.concertDateId(concertDateId)
			.seatNo(10)
			.price(BigDecimal.valueOf(50000))
			.seatClass(SeatClass.VIP)
			.status(SeatStatus.RESERVED) // 이미 예약된 상태
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationDomainService.processReservation(concertDate, reservedSeat, userId));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_RESERVED_SEAT);
	}

	@Test
	@DisplayName("예약_처리_실패_좌석이_배정된_상태")
	void processReservation_Failure_SeatAssigned() throws CustomException {
		Seat assignedSeat = Seat.builder()
			.id(seatId)
			.concertDateId(concertDateId)
			.seatNo(10)
			.price(BigDecimal.valueOf(50000))
			.seatClass(SeatClass.VIP)
			.status(SeatStatus.ASSIGNED)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationDomainService.processReservation(concertDate, assignedSeat, userId));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_RESERVED_SEAT);
	}

	@Test
	@DisplayName("예약_처리_실패_콘서트_마감시간_초과")
	void processReservation_Failure_OverDeadline() throws CustomException {
		ConcertDate expiredConcertDate = ConcertDate.builder()
			.id(concertDateId)
			.concertId(concertId)
			.date(LocalDateTime.now().plusDays(7))
			.deadline(LocalDateTime.now().minusHours(1))
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationDomainService.processReservation(expiredConcertDate, availableSeat, userId));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.OVER_DEADLINE);
	}

	@Test
	@DisplayName("예약_처리_성공_마감시간_경계값_테스트")
	void processReservation_Success_DeadlineBoundaryTest() throws CustomException {
		ConcertDate nearDeadlineConcertDate = ConcertDate.builder()
			.id(concertDateId)
			.concertId(concertId)
			.date(LocalDateTime.now().plusDays(7))
			.deadline(LocalDateTime.now().plusMinutes(1))
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		assertThatNoException().isThrownBy(() ->
			reservationDomainService.processReservation(nearDeadlineConcertDate, availableSeat, userId)
		);

		ConcertDate pastDeadlineConcertDate = ConcertDate.builder()
			.id(concertDateId)
			.concertId(concertId)
			.date(LocalDateTime.now().plusDays(7))
			.deadline(LocalDateTime.now().minusMinutes(1))
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationDomainService.processReservation(pastDeadlineConcertDate, availableSeat, userId));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.OVER_DEADLINE);
	}
}