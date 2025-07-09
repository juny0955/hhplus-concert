package kr.hhplus.be.server.api.payment;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.testcontainers.utility.TestcontainersConfiguration;

import kr.hhplus.be.server.api.TestDataFactory;
import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.concert.port.out.ConcertRepository;
import kr.hhplus.be.server.domain.concertDate.domain.ConcertDate;
import kr.hhplus.be.server.domain.concertDate.port.out.ConcertDateRepository;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.payment.port.out.PaymentRepository;
import kr.hhplus.be.server.domain.payment.domain.PaymentStatus;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.domain.queue.port.out.QueueTokenRepository;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.reservation.port.out.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.domain.ReservationStatus;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.seat.domain.SeatStatus;
import kr.hhplus.be.server.domain.user.domain.User;

@SpringBootTest(properties = {
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class PaymentConcurrencyTest {

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
	private PaymentRepository paymentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ConcertRepository concertRepository;

	@Autowired
	private ConcertDateRepository concertDateRepository;

	@Autowired
	private SeatRepository seatRepository;

	private static final int THREAD_SIZE = 5;

	private UUID concertId;
	private UUID concertDateId;
	private UUID userId;
	private UUID seatId;
	private UUID reservationId;
	private UUID activeTokenId;

	private User user;

	@BeforeEach
	void beforeEach() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();

		user = TestDataFactory.createUserWithAmount(BigDecimal.valueOf(1_000_000));
		User savedUser = userRepository.save(user);
		userId = savedUser.id();

		Concert concert = TestDataFactory.createConcert();
		Concert savedConcert = concertRepository.save(concert);
		concertId = savedConcert.id();

		ConcertDate concertDate = TestDataFactory.createConcertDate(concertId);
		ConcertDate savedConcertDate = concertDateRepository.save(concertDate);
		concertDateId = savedConcertDate.id();

		Seat seat = TestDataFactory.createReservedSeat(concertDateId);
		Seat savedSeat = seatRepository.save(seat);
		seatId = savedSeat.id();

		Reservation reservation = TestDataFactory.createReservation(userId, seatId);
		Reservation savedReservation = reservationRepository.save(reservation);
		reservationId = savedReservation.id();

		Payment payment = TestDataFactory.createPayment(userId, reservationId);
		paymentRepository.save(payment);

		activeTokenId = UUID.randomUUID();
		QueueToken queueToken = QueueToken.activeTokenOf(activeTokenId, userId, concertId, 1000000L);
		queueTokenRepository.save(queueToken);

		seatHoldRepository.hold(seatId, userId);
	}

	@Test
	@DisplayName("동시_결제")
	void payment_Concurrency_Test() throws Exception {
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		AtomicInteger successCount = new AtomicInteger(0);

		for (int i = 0; i < THREAD_SIZE; i++) {
			futures.add(CompletableFuture.runAsync(() -> {
				try {
					int status = mockMvc.perform(post("/api/v1/payments/{reservationId}", reservationId)
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", activeTokenId))
						.andReturn()
						.getResponse()
						.getStatus();

					if (status == 200)
						successCount.incrementAndGet();

				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}));
		}

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(10, TimeUnit.SECONDS);

		Payment updatedPayment = paymentRepository.findByReservationId(reservationId).get();
		assertThat(updatedPayment.status()).isEqualTo(PaymentStatus.SUCCESS);

		Reservation updatedReservation = reservationRepository.findById(reservationId).get();
		assertThat(updatedReservation.status()).isEqualTo(ReservationStatus.SUCCESS);

		Seat updatedSeat = seatRepository.findById(seatId).get();
		assertThat(updatedSeat.status()).isEqualTo(SeatStatus.ASSIGNED);

		User updatedUser = userRepository.findById(userId).get();
		assertThat(updatedUser.amount()).isEqualTo(user.amount().subtract(updatedSeat.price()));

		assertThat(successCount.get()).isEqualTo(1);
	}
}