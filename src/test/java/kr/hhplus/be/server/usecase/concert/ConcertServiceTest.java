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

import kr.hhplus.be.server.entity.concert.ConcertDate;
import kr.hhplus.be.server.entity.concert.Seat;
import kr.hhplus.be.server.entity.concert.SeatClass;
import kr.hhplus.be.server.entity.concert.SeatStatus;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.ConcertDateEntity;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.ConcertEntity;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.SeatEntity;
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
	private ConcertEntity concertEntity;
	private ConcertDateEntity concertDateEntity;
	private SeatEntity seatEntity;

	@BeforeEach
	void beforeEach() {
		concertId = UUID.randomUUID();
		concertDateId = UUID.randomUUID();
		seatId = UUID.randomUUID();

		concertEntity = ConcertEntity.builder()
			.id(concertId.toString())
			.title("GD 콘서트")
			.artist("GD")
			.build();

		concertDateEntity = ConcertDateEntity.builder()
			.id(concertDateId.toString())
			.concert(concertEntity)
			.date(LocalDateTime.now().plusDays(7))
			.deadline(LocalDateTime.now().plusDays(5))
			.build();

		seatEntity = SeatEntity.builder()
			.id(seatId.toString())
			.concertDate(concertDateEntity)
			.seatNo(1)
			.seatClass(SeatClass.VIP)
			.status(SeatStatus.AVAILABLE)
			.price(BigDecimal.valueOf(100_000))
			.build();
	}


	@Test
	@DisplayName("예약_가능_콘서트_날짜_조회_성공")
	void getAvailableConcertDates_Success() throws CustomException {
		List<ConcertDateEntity> concertDateEntities = List.of(concertDateEntity);

		when(concertRepository.findById(concertId.toString())).thenReturn(Optional.of(concertEntity));
		when(concertDateRepository.findAvailableDates(concertEntity.getId())).thenReturn(concertDateEntities);
		when(seatRepository.countRemainingSeat(concertDateEntity.getId())).thenReturn(concertDateEntities.size());

		List<ConcertDate> results = concertService.getAvailableConcertDates(concertId);

		verify(concertRepository, times(1)).findById(concertId.toString());
		verify(concertDateRepository, times(1)).findAvailableDates(concertEntity.getId());
		verify(seatRepository, times(concertDateEntities.size())).countRemainingSeat(concertDateEntity.getId());

		assertThat(results).hasSize(1);
		assertThat(results.get(0).id()).isEqualTo(concertDateId);
	}

	@Test
	@DisplayName("예약_가능_콘서트_날짜_조회_실패_콘서트못찾음")
	void getAvailableConcertDates_Failure_ConcertNotFound() {
		when(concertRepository.findById(concertId.toString())).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> concertService.getAvailableConcertDates(concertId));

		verify(concertRepository, times(1)).findById(concertId.toString());
		verify(concertDateRepository, never()).findAvailableDates(concertEntity.getId());
		verify(seatRepository, never()).countRemainingSeat(concertDateEntity.getId());

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
	}

	@Test
	@DisplayName("예약_가능_콘서트_날짜_조회_정상_빈_리스트(매진, 예약)")
	void getAvailableConcertDates_Success_CanReservationDateNotFound() throws CustomException {
		when(concertRepository.findById(concertId.toString())).thenReturn(Optional.of(concertEntity));
		when(concertDateRepository.findAvailableDates(concertEntity.getId())).thenReturn(Collections.emptyList());

		List<ConcertDate> results = concertService.getAvailableConcertDates(concertId);

		verify(concertRepository, times(1)).findById(concertId.toString());
		verify(concertDateRepository, times(1)).findAvailableDates(concertEntity.getId());
		verify(seatRepository, times(0)).countRemainingSeat(concertDateEntity.getId());
		assertThat(results).isEmpty();
	}

	@Test
	@DisplayName("예약_가능_좌석_조회_정상")
	void getAvailableSeats_Success() throws CustomException {
		when(concertRepository.findById(concertId.toString())).thenReturn(Optional.of(concertEntity));
		when(concertDateRepository.findAvailableDate(concertEntity.getId(), concertDateId.toString())).thenReturn(Optional.of(concertDateEntity));
		when(seatRepository.findAvailableSeats(concertDateEntity.getId())).thenReturn(List.of(seatEntity));

		List<Seat> results = concertService.getAvailableSeats(concertId, concertDateId);

		verify(concertRepository, times(1)).findById(concertId.toString());
		verify(concertDateRepository, times(1)).findAvailableDate(concertEntity.getId(), concertDateId.toString());
		verify(seatRepository, times(1)).findAvailableSeats(concertDateEntity.getId());

		assertThat(results).hasSize(1);
		assertThat(results.get(0).id()).isEqualTo(seatId);
	}

	@Test
	@DisplayName("예약_가능_좌석_조회_실패_콘서트찾을수없음")
	void getAvailableSeats_Failure_ConcertNotFound() {
		when(concertRepository.findById(concertId.toString())).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> concertService.getAvailableSeats(concertId, concertDateId));

		verify(concertRepository, times(1)).findById(concertId.toString());
		verify(concertDateRepository, never()).findAvailableDate(concertEntity.getId(), concertDateId.toString());
		verify(seatRepository, never()).findAvailableSeats(concertDateEntity.getId());

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
	}

	@Test
	@DisplayName("예약_가능_좌석_조회_실패_해당날짜예약불가")
	void getAvailableSeats_Failure_CannotReservationDate() {
		when(concertRepository.findById(concertId.toString())).thenReturn(Optional.of(concertEntity));
		when(concertDateRepository.findAvailableDate(concertEntity.getId(), concertDateId.toString())).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> concertService.getAvailableSeats(concertId, concertDateId));

		verify(concertRepository, times(1)).findById(concertId.toString());
		verify(concertDateRepository, times(1)).findAvailableDate(concertEntity.getId(), concertDateId.toString());
		verify(seatRepository, never()).findAvailableSeats(concertDateEntity.getId());

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CANNOT_RESERVATION_DATE);
	}

	@Test
	@DisplayName("예약_가능_좌석_조회_정상_빈_리스트(매진, 예약)")
	void getAvailableSeats_Success_EmptyList() throws CustomException {
		when(concertRepository.findById(concertId.toString())).thenReturn(Optional.of(concertEntity));
		when(concertDateRepository.findAvailableDate(concertEntity.getId(), concertDateId.toString())).thenReturn(Optional.of(concertDateEntity));
		when(seatRepository.findAvailableSeats(concertDateId.toString())).thenReturn(Collections.emptyList());

		List<Seat> results = concertService.getAvailableSeats(concertId, concertDateId);

		verify(concertRepository, times(1)).findById(concertId.toString());
		verify(concertDateRepository, times(1)).findAvailableDate(concertEntity.getId(), concertDateId.toString());
		verify(seatRepository, times(1)).findAvailableSeats(concertDateEntity.getId());

		assertThat(results).isEmpty();
	}
}