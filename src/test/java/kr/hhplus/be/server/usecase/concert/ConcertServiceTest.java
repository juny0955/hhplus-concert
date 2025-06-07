package kr.hhplus.be.server.usecase.concert;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertDate;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.concert.SeatClass;
import kr.hhplus.be.server.domain.concert.SeatStatus;
import kr.hhplus.be.server.usecase.exception.CustomException;
import kr.hhplus.be.server.usecase.exception.ErrorCode;

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

		when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
		when(concertDateRepository.findAvailableDates(concertId)).thenReturn(concertDateEntities);
		when(seatRepository.countRemainingSeat(concertDateId)).thenReturn(50);

		List<ConcertDate> results = concertService.getAvailableConcertDates(concertId);

		verify(concertRepository, times(1)).findById(concertId);
		verify(concertDateRepository, times(1)).findAvailableDates(concert.id());
		verify(seatRepository, times(concertDateEntities.size())).countRemainingSeat(concertDate.id());

		assertThat(results).hasSize(1);
		assertThat(results.get(0).id()).isEqualTo(concertDateId);
	}

	@Test
	@DisplayName("예약_가능_콘서트_날짜_조회_실패_콘서트못찾음")
	void getAvailableConcertDates_Failure_ConcertNotFound() {
		when(concertRepository.findById(concertId)).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> concertService.getAvailableConcertDates(concertId));

		verify(concertRepository, times(1)).findById(concertId);
		verify(concertDateRepository, never()).findAvailableDates(concert.id());
		verify(seatRepository, never()).countRemainingSeat(concertDate.id());

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
	}

	@Test
	@DisplayName("예약_가능_콘서트_날짜_조회_정상_빈_리스트(매진, 예약)")
	void getAvailableConcertDates_Success_CanReservationDateNotFound() throws CustomException {
		when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
		when(concertDateRepository.findAvailableDates(concertId)).thenReturn(Collections.emptyList());

		List<ConcertDate> results = concertService.getAvailableConcertDates(concertId);

		verify(concertRepository, times(1)).findById(concertId);
		verify(concertDateRepository, times(1)).findAvailableDates(concertId);
		verify(seatRepository, never()).countRemainingSeat(any());

		assertThat(results).isEmpty();
	}

	@Test
	@DisplayName("에약_가능_콘서트_날짜_조회_정상_빈_리스트(가능한 좌석 수 0)")
	void getAvailableConcertDates_Success_CanReservationSeatsZero() throws CustomException {
		List<ConcertDate> concertDates = List.of(concertDate);

		when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
		when(concertDateRepository.findAvailableDates(concertId)).thenReturn(concertDates);
		when(seatRepository.countRemainingSeat(concertDateId)).thenReturn(0);

		List<ConcertDate> results = concertService.getAvailableConcertDates(concertId);

		verify(concertRepository, times(1)).findById(concertId);
		verify(concertDateRepository, times(1)).findAvailableDates(concertId);
		verify(seatRepository, times(1)).countRemainingSeat(concertDateId);

		assertThat(results).isEmpty();
	}

	@Test
	@DisplayName("예약_가능_좌석_조회_정상")
	void getAvailableSeats_Success() throws CustomException {
		when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
		when(concertDateRepository.findAvailableDate(concertId, concertDateId)).thenReturn(Optional.of(concertDate));
		when(seatRepository.findAvailableSeats(concertDateId)).thenReturn(List.of(seat));

		List<Seat> results = concertService.getAvailableSeats(concertId, concertDateId);

		verify(concertRepository, times(1)).findById(concertId);
		verify(concertDateRepository, times(1)).findAvailableDate(concertId, concertDateId);
		verify(seatRepository, times(1)).findAvailableSeats(concertDateId);

		assertThat(results).hasSize(1);
		assertThat(results.get(0).id()).isEqualTo(seatId);
	}

	@Test
	@DisplayName("예약_가능_좌석_조회_실패_콘서트찾을수없음")
	void getAvailableSeats_Failure_ConcertNotFound() {
		when(concertRepository.findById(concertId)).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> concertService.getAvailableSeats(concertId, concertDateId));

		verify(concertRepository, times(1)).findById(concertId);
		verify(concertDateRepository, never()).findAvailableDate(any(), any());
		verify(seatRepository, never()).findAvailableSeats(any());

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
	}

	@Test
	@DisplayName("예약_가능_좌석_조회_실패_해당날짜예약불가")
	void getAvailableSeats_Failure_CannotReservationDate() {
		when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
		when(concertDateRepository.findAvailableDate(concertId, concertDateId)).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> concertService.getAvailableSeats(concertId, concertDateId));

		verify(concertRepository, times(1)).findById(concertId);
		verify(concertDateRepository, times(1)).findAvailableDate(concertId, concertDateId);
		verify(seatRepository, never()).findAvailableSeats(any());

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CANNOT_RESERVATION_DATE);
	}

	@Test
	@DisplayName("예약_가능_좌석_조회_정상_빈_리스트(매진, 예약)")
	void getAvailableSeats_Success_EmptyList() throws CustomException {
		when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
		when(concertDateRepository.findAvailableDate(concertId, concertDateId)).thenReturn(Optional.of(concertDate));
		when(seatRepository.findAvailableSeats(concertDateId)).thenReturn(Collections.emptyList());

		List<Seat> results = concertService.getAvailableSeats(concertId, concertDateId);

		verify(concertRepository, times(1)).findById(concertId);
		verify(concertDateRepository, times(1)).findAvailableDate(concertId, concertDateId);
		verify(seatRepository, times(1)).findAvailableSeats(concertDateId);

		assertThat(results).isEmpty();
	}
}