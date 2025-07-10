package kr.hhplus.be.server.usecase.concert;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.concert.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.concert.domain.concertDate.ConcertDates;
import kr.hhplus.be.server.domain.concert.domain.seat.Seat;
import kr.hhplus.be.server.domain.concert.domain.seat.SeatClass;
import kr.hhplus.be.server.domain.concert.domain.seat.SeatStatus;
import kr.hhplus.be.server.domain.concert.domain.seat.Seats;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

	@InjectMocks
	private ConcertService concertService;

	@Mock
	private ConcertRepository concertRepository;

	@Mock
	private ConcertDateRepository concertDateRepository;

	@Mock
	private SeatRepository seatRepository;

	private UUID concertId;
	private UUID concertDateId;
	private UUID seatId;
	private Concert concert;
	private ConcertDate concertDate;
	private Seat seat;

	@BeforeEach
	void beforeEach() {
		concertId = UUID.randomUUID();
		concertDateId = UUID.randomUUID();
		seatId = UUID.randomUUID();

		concert = Concert.builder()
			.id(concertId)
			.title("GD 콘서트")
			.artist("GD")
			.build();

		concertDate = ConcertDate.builder()
			.id(concertDateId)
			.concertId(concertId)
			.date(LocalDateTime.now().plusDays(7))
			.deadline(LocalDateTime.now().plusDays(5))
			.build();

		seat = Seat.builder()
			.id(seatId)
			.concertDateId(concertDateId)
			.seatNo(1)
			.seatClass(SeatClass.VIP)
			.status(SeatStatus.AVAILABLE)
			.price(BigDecimal.valueOf(100_000))
			.build();
	}


	@Test
	@DisplayName("예약_가능_콘서트_날짜_조회_성공")
	void getAvailableConcertDates_Success() throws CustomException {
		List<ConcertDate> concertDateEntities = List.of(concertDate);
		ConcertDates concertDates = new ConcertDates(concertDateEntities);

		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(concertDateRepository.findAvailableDatesWithAvailableSeatCount(concertId)).thenReturn(concertDates);

		List<ConcertDate> results = concertService.getAvailableConcertDates(concertId);

		verify(concertRepository, times(1)).existsById(concertId);
		verify(concertDateRepository, times(1)).findAvailableDatesWithAvailableSeatCount(concert.id());

		assertThat(results).hasSize(1);
		assertThat(results.get(0).id()).isEqualTo(concertDateId);
	}

	@Test
	@DisplayName("예약_가능_콘서트_날짜_조회_실패_콘서트못찾음")
	void getAvailableConcertDates_Failure_ConcertNotFound() {
		when(concertRepository.existsById(concertId)).thenReturn(false);

		CustomException customException = assertThrows(CustomException.class,
			() -> concertService.getAvailableConcertDates(concertId));

		verify(concertRepository, times(1)).existsById(concertId);
		verify(concertDateRepository, never()).findAvailableDatesWithAvailableSeatCount(concert.id());

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
	}

	@Test
	@DisplayName("예약_가능_콘서트_날짜_조회_정상_빈_리스트(매진, 예약)")
	void getAvailableConcertDates_Success_CanReservationDateNotFound() throws CustomException {
		ConcertDates concertDates = new ConcertDates(Collections.emptyList());

		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(concertDateRepository.findAvailableDatesWithAvailableSeatCount(concertId)).thenReturn(concertDates);

		List<ConcertDate> results = concertService.getAvailableConcertDates(concertId);

		verify(concertRepository, times(1)).existsById(concertId);
		verify(concertDateRepository, times(1)).findAvailableDatesWithAvailableSeatCount(concertId);

		assertThat(results).isEmpty();
	}

	@Test
	@DisplayName("예약_가능_좌석_조회_정상")
	void getAvailableSeats_Success() throws CustomException {
		Seats seats = new Seats(List.of(seat));

		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(seatRepository.findAvailableSeats(concertId, concertDateId)).thenReturn(seats);

		List<Seat> results = concertService.getAvailableSeats(concertId, concertDateId);

		verify(concertRepository, times(1)).existsById(concertId);
		verify(seatRepository, times(1)).findAvailableSeats(concertId, concertDateId);
		verify(concertDateRepository, never()).existsById(concertId);

		assertThat(results).hasSize(1);
		assertThat(results.get(0).id()).isEqualTo(seatId);
	}

	@Test
	@DisplayName("예약_가능_좌석_조회_실패_콘서트찾을수없음")
	void getAvailableSeats_Failure_ConcertNotFound() {
		when(concertRepository.existsById(concertId)).thenReturn(false);

		CustomException customException = assertThrows(CustomException.class,
			() -> concertService.getAvailableSeats(concertId, concertDateId));

		verify(concertRepository, times(1)).existsById(concertId);
		verify(seatRepository, never()).findAvailableSeats(concertId, concertDateId);
		verify(concertDateRepository, never()).existsById(concertDateId);

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
	}

	@Test
	@DisplayName("예약_가능_좌석_조회_실패_해당날짜예약불가")
	void getAvailableSeats_Failure_CannotReservationDate() {
		Seats seats = new Seats(Collections.emptyList());

		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(seatRepository.findAvailableSeats(concertId, concertDateId)).thenReturn(seats);
		when(concertDateRepository.existsById(concertDateId)).thenReturn(false);

		CustomException customException = assertThrows(CustomException.class,
			() -> concertService.getAvailableSeats(concertId, concertDateId));

		verify(concertRepository, times(1)).existsById(concertId);
		verify(seatRepository, times(1)).findAvailableSeats(concertId, concertDateId);
		verify(concertDateRepository, times(1)).existsById(concertDateId);

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CANNOT_RESERVATION_DATE);
	}

	@Test
	@DisplayName("예약_가능_좌석_조회_정상_빈_리스트(매진, 예약)")
	void getAvailableSeats_Success_EmptyList() throws CustomException {
		Seats seats = new Seats(Collections.emptyList());

		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(seatRepository.findAvailableSeats(concertId, concertDateId)).thenReturn(seats);
		when(concertDateRepository.existsById(concertDateId)).thenReturn(true);

		List<Seat> results = concertService.getAvailableSeats(concertId, concertDateId);

		verify(concertRepository, times(1)).existsById(concertId);
		verify(seatRepository, times(1)).findAvailableSeats(concertId, concertDateId);
		verify(concertDateRepository, times(1)).existsById(concertDateId);

		assertThat(results).isEmpty();
	}
}