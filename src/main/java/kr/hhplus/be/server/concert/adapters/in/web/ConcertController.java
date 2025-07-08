package kr.hhplus.be.server.concert.adapters.in.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.concert.adapters.in.web.response.ConcertDateResponse;
import kr.hhplus.be.server.concert.adapters.in.web.response.SeatResponse;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.ports.in.concertDate.GetAvailableConcertDatesInput;
import kr.hhplus.be.server.concert.ports.in.seat.GetAvailableSeatsInput;
import kr.hhplus.be.server.common.framework.exception.CustomException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/concerts")
@RequiredArgsConstructor
@Tag(name = "Concert API", description = "콘서트 관련 API")
public class ConcertController {

	private final GetAvailableSeatsInput getAvailableSeatsInput;
	private final GetAvailableConcertDatesInput getAvailableConcertDatesInput;

	@Operation(
		summary = "콘서트 예약 가능 날짜 조회 API",
		description = "헤당 콘서트 예약 가능한 날짜 조회"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "조회 성공",
			content = @Content(schema = @Schema(implementation = ConcertDateResponse.class))
		),
		@ApiResponse(
			responseCode = "404 - Concert",
			description = "콘서트 찾을 수 없음"
		),
	})
	@GetMapping("/{concertId}/dates")
	public ResponseEntity<List<ConcertDateResponse>> getAvailableDates(
		@PathVariable UUID concertId
	) throws CustomException {
		List<ConcertDate> availableConcertDates = getAvailableConcertDatesInput.getAvailableConcertDates(concertId);
		List<ConcertDateResponse> responses = availableConcertDates.stream()
			.map(ConcertDateResponse::from)
			.toList();

		return ResponseEntity.ok(responses);
	}

	@Operation(
		summary = "콘서트 예약 가능 좌석 조회 API",
		description = "해당 콘서트, 해당 날짜의 예약 가능한 좌석 조회"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "조회 성공",
			content = @Content(schema = @Schema(implementation = SeatResponse.class))
		),
		@ApiResponse(
			responseCode = "404 - Concert",
			description = "콘서트 찾을 수 없음"
		),
		@ApiResponse(
			responseCode = "404 - ConcertDate",
			description = "콘서트 날짜 찾을 수 없음"
		),
	})
	@GetMapping("/{concertId}/dates/{concertDateId}/seats")
	public ResponseEntity<List<SeatResponse>> getAvailableSeats(
		@PathVariable UUID concertId,
		@PathVariable UUID concertDateId
	) throws CustomException {
		List<Seat> availableSeats = getAvailableSeatsInput.getAvailableSeats(concertId, concertDateId);
		List<SeatResponse> response = availableSeats.stream()
			.map(SeatResponse::from)
			.toList();

		return ResponseEntity.ok(response);
	}
}
