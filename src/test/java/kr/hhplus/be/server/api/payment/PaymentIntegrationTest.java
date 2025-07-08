package kr.hhplus.be.server.api.payment;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import kr.hhplus.be.server.api.TestDataFactory;
import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.concert.ports.out.ConcertRepository;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.concert.ports.out.ConcertDateRepository;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.ports.out.PaymentRepository;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.queue.domain.QueueStatus;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.ports.out.QueueTokenRepository;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.ports.out.ReservationRepository;
import kr.hhplus.be.server.reservation.domain.ReservationStatus;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.ports.out.SeatHoldRepository;
import kr.hhplus.be.server.concert.ports.out.SeatRepository;
import kr.hhplus.be.server.concert.domain.seat.SeatStatus;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.ports.out.UserRepository;
import kr.hhplus.be.server.framework.exception.ErrorCode;

@SpringBootTest(properties = {
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
class PaymentIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private QueueTokenRepository queueTokenRepository;

	@Autowired
	private SeatHoldRepository seatHoldRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SeatRepository seatRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private ConcertRepository concertRepository;

	@Autowired
	private ConcertDateRepository concertDateRepository;

	private UUID userId;
	private UUID concertId;
	private UUID concertDateId;
	private UUID seatId;
	private UUID reservationId;
	private UUID paymentId;
	private UUID activeTokenId;

	private User user;
	private Concert concert;
	private ConcertDate concertDate;
	private Seat seat;
	private Reservation reservation;
	private Payment payment;
	private QueueToken queueToken;

	@BeforeEach
	void beforeEach() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();

		user = TestDataFactory.createUser();
		User savedUser = userRepository.save(user);
		userId = savedUser.id();

		concert = TestDataFactory.createConcert();
		Concert savedConcert = concertRepository.save(concert);
		concertId = savedConcert.id();

		concertDate = TestDataFactory.createConcertDate(concertId);
		ConcertDate savedConcertDate = concertDateRepository.save(concertDate);
		concertDateId = savedConcertDate.id();

		seat = TestDataFactory.createSeat(concertDateId);
		Seat savedSeat = seatRepository.save(seat);
		seatId = savedSeat.id();

		reservation = TestDataFactory.createReservation(userId, seatId);
		Reservation savedReservation = reservationRepository.save(reservation);
		reservationId = savedReservation.id();

		payment = TestDataFactory.createPayment(userId, reservationId);
		Payment savedPayment = paymentRepository.save(payment);
		paymentId = savedPayment.id();

		activeTokenId = UUID.randomUUID();
		queueToken = QueueToken.activeTokenOf(activeTokenId, userId, concertId, 1000000L);
		queueTokenRepository.save(queueToken);

		seatHoldRepository.hold(seatId, userId);
	}

	@Test
	@DisplayName("결제_성공")
	void payment_Success() throws Exception {
		mockMvc.perform(post("/api/v1/payments/{reservationId}", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", activeTokenId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.paymentId").exists())
			.andExpect(jsonPath("$.reservationId").value(reservationId.toString()))
			.andExpect(jsonPath("$.amount").value(50000))
			.andExpect(jsonPath("$.status").value(PaymentStatus.SUCCESS.toString()))
			.andReturn();


		Payment updatedPayment = paymentRepository.findByReservationId(reservationId).get();
		assertThat(updatedPayment.status()).isEqualTo(PaymentStatus.SUCCESS);

		Reservation updatedReservation = reservationRepository.findById(reservationId).get();
		assertThat(updatedReservation.status()).isEqualTo(ReservationStatus.SUCCESS);

		Seat updatedSeat = seatRepository.findById(seatId).get();
		assertThat(updatedSeat.status()).isEqualTo(SeatStatus.ASSIGNED);

		User updatedUser = userRepository.findById(userId).get();
		assertThat(updatedUser.amount()).isEqualTo(BigDecimal.valueOf(50000)); // 100000 - 50000

		assertThat(seatHoldRepository.hasHoldByUser(seatId, userId)).isFalse();

		QueueToken expiredToken = queueTokenRepository.findQueueTokenByTokenId(activeTokenId.toString());
		assertThat(expiredToken).isNull();
	}

	@Test
	@DisplayName("결제_실패_대기열토큰활성상태아님")
	void payment_Failure_InvalidQueueToken() throws Exception {
		UUID waitingTokenId = UUID.randomUUID();
		QueueToken waitingToken = QueueToken.waitingTokenOf(waitingTokenId, userId, concertId, 1);
		queueTokenRepository.save(waitingToken);

		mockMvc.perform(post("/api/v1/payments/{reservationId}", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", waitingTokenId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(ErrorCode.INVALID_QUEUE_TOKEN.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.INVALID_QUEUE_TOKEN.getMessage()));

		Payment unchangedPayment = paymentRepository.findByReservationId(reservationId).get();
		assertThat(unchangedPayment.status()).isEqualTo(PaymentStatus.PENDING);
	}

	@Test
	@DisplayName("결제_실패_토큰없음")
	void payment_Failure_TokenNotFound() throws Exception {
		UUID nonExistentTokenId = UUID.randomUUID();

		mockMvc.perform(post("/api/v1/payments/{reservationId}", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", nonExistentTokenId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(ErrorCode.INVALID_QUEUE_TOKEN.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.INVALID_QUEUE_TOKEN.getMessage()));
	}

	@Test
	@DisplayName("결제_실패_예약정보찾지못함")
	void payment_Failure_ReservationNotFound() throws Exception {
		UUID nonExistentReservationId = UUID.randomUUID();

		mockMvc.perform(post("/api/v1/payments/{reservationId}", nonExistentReservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", activeTokenId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorCode.RESERVATION_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.RESERVATION_NOT_FOUND.getMessage()));
	}

	@Test
	@DisplayName("결제_실패_결제정보찾지못함")
	void payment_Failure_PaymentNotFound() throws Exception {
		Reservation otherReservation = TestDataFactory.createReservation(userId, seatId);
		Reservation savedOtherReservation = reservationRepository.save(otherReservation);

		mockMvc.perform(post("/api/v1/payments/{reservationId}", savedOtherReservation.id())
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", activeTokenId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorCode.PAYMENT_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.PAYMENT_NOT_FOUND.getMessage()));
	}

	@Test
	@DisplayName("결제_실패_사용자잔액부족")
	void payment_Failure_InsufficientBalance() throws Exception {
		User poorUser = TestDataFactory.createPoorUser();
		User savedPoorUser = userRepository.save(poorUser);

		Reservation poorUserReservation = TestDataFactory.createReservation(savedPoorUser.id(), seatId);
		Reservation savedPoorUserReservation = reservationRepository.save(poorUserReservation);

		Payment poorUserPayment = TestDataFactory.createPayment(savedPoorUser.id(), savedPoorUserReservation.id());
		paymentRepository.save(poorUserPayment);

		UUID poorUserTokenId = UUID.randomUUID();
		QueueToken poorUserToken = QueueToken.activeTokenOf(poorUserTokenId, savedPoorUser.id(), concertId, 1000000L);
		queueTokenRepository.save(poorUserToken);

		seatHoldRepository.hold(seatId, savedPoorUser.id());

		mockMvc.perform(post("/api/v1/payments/{reservationId}", savedPoorUserReservation.id())
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", poorUserTokenId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(ErrorCode.INSUFFICIENT_BALANCE.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.INSUFFICIENT_BALANCE.getMessage()));
	}

	@Test
	@DisplayName("결제_실패_좌석임시배정끝남")
	void payment_Failure_SeatNotHold() throws Exception {
		seatHoldRepository.deleteHold(seatId, userId);

		mockMvc.perform(post("/api/v1/payments/{reservationId}", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", activeTokenId))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value(ErrorCode.SEAT_NOT_HOLD.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.SEAT_NOT_HOLD.getMessage()));
	}

	@Test
	@DisplayName("결제_실패_이미결제됨")
	void payment_Failure_AlreadyPaid() throws Exception {
		Payment existingPayment = paymentRepository.findByReservationId(reservationId).get();
		Payment paidPayment = existingPayment.success();
		paymentRepository.save(paidPayment);

		mockMvc.perform(post("/api/v1/payments/{reservationId}", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", activeTokenId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(ErrorCode.ALREADY_PAID.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_PAID.getMessage()));
	}

	@Test
	@DisplayName("결제_실패_유저정보찾지못함")
	void payment_Failure_UserNotFound() throws Exception {
		UUID otherUserId = UUID.randomUUID();
		UUID otherTokenId = UUID.randomUUID();
		QueueToken otherQueueToken = QueueToken.activeTokenOf(otherTokenId, otherUserId, concertId, 1000000L);
		queueTokenRepository.save(otherQueueToken);

		Reservation otherReservation = TestDataFactory.createReservation(otherUserId, seatId);
		Reservation savedNonUserReservation = reservationRepository.save(otherReservation);

		Payment otherPayment = TestDataFactory.createPayment(otherUserId, savedNonUserReservation.id());
		paymentRepository.save(otherPayment);

		seatHoldRepository.hold(seatId, otherUserId);

		mockMvc.perform(post("/api/v1/payments/{reservationId}", savedNonUserReservation.id())
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", otherTokenId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));
	}

	@Test
	@DisplayName("결제_실패_좌석정보찾지못함")
	void payment_Failure_SeatNotFound() throws Exception {
		UUID otherSeatId = UUID.randomUUID();
		Reservation invalidSeatReservation = TestDataFactory.createReservation(userId, otherSeatId);
		Reservation savedInvalidSeatReservation = reservationRepository.save(invalidSeatReservation);

		Payment invalidSeatPayment = TestDataFactory.createPayment(otherSeatId, savedInvalidSeatReservation.id());
		paymentRepository.save(invalidSeatPayment);

		seatHoldRepository.hold(otherSeatId, userId);

		mockMvc.perform(post("/api/v1/payments/{reservationId}", savedInvalidSeatReservation.id())
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", activeTokenId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorCode.SEAT_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.SEAT_NOT_FOUND.getMessage()));
	}

	@Test
	@DisplayName("결제_실패_만료된토큰")
	void payment_Failure_ExpiredToken() throws Exception {
		UUID expiredTokenId = UUID.randomUUID();
		QueueToken expiredToken = QueueToken.builder()
			.tokenId(expiredTokenId)
			.userId(userId)
			.concertId(concertId)
			.status(QueueStatus.ACTIVE)
			.position(0)
			.issuedAt(LocalDateTime.now().minusHours(2))
			.enteredAt(LocalDateTime.now().minusHours(2))
			.expiresAt(LocalDateTime.now().minusHours(1)) // 1시간 전에 만료
			.build();
		queueTokenRepository.save(expiredToken);

		mockMvc.perform(post("/api/v1/payments/{reservationId}", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", expiredTokenId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(ErrorCode.INVALID_QUEUE_TOKEN.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.INVALID_QUEUE_TOKEN.getMessage()));
	}

	@Test
	@DisplayName("결제_실패_0원결제")
	void payment_Failure_ZeroAmountPayment() throws Exception {
		Payment zeroPayment = Payment.builder()
			.id(paymentId)
			.userId(userId)
			.reservationId(reservationId)
			.amount(BigDecimal.ZERO)
			.status(PaymentStatus.PENDING)
			.build();
		paymentRepository.save(zeroPayment);

		mockMvc.perform(post("/api/v1/payments/{reservationId}", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", activeTokenId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(ErrorCode.INVALID_PAYMENT_AMOUNT.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.INVALID_PAYMENT_AMOUNT.getMessage()));
	}
}