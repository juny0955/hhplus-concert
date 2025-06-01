package kr.hhplus.be.server.interfaces.api.concert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.entity.concert.ConcertDate;
import kr.hhplus.be.server.entity.concert.Seat;
import kr.hhplus.be.server.entity.reservation.ReservationStatus;
import kr.hhplus.be.server.interfaces.api.concert.dto.request.QueueTokenRequest;
import kr.hhplus.be.server.interfaces.api.concert.dto.request.ReservationRequest;
import kr.hhplus.be.server.interfaces.api.concert.dto.response.ConcertDateResponse;
import kr.hhplus.be.server.interfaces.api.concert.dto.response.QueueTokenResponse;
import kr.hhplus.be.server.interfaces.api.concert.dto.response.ReservationResponse;
import kr.hhplus.be.server.interfaces.api.concert.dto.response.SeatResponse;
import kr.hhplus.be.server.usecase.concert.ConcertService;
import kr.hhplus.be.server.usecase.exception.CustomException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/concerts")
@RequiredArgsConstructor
@Tag(name = "Concert API", description = "콘서트 관련 API")
public class ConcertController {

	private final ConcertService concertService;

	@Operation(
		summary = "콘서트 대기열 토큰 발급",
		description = "서비스 사용 하기 위한 콘서트 별 대기열 토큰 발급"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201",
			description = "토큰 발급 성공",
			content = @Content(schema = @Schema(implementation = QueueTokenResponse.class))
		),
		@ApiResponse(
			responseCode = "404 - User",
			description = "유저 찾을 수 없음"
		),
		@ApiResponse(
			responseCode = "404 - Concert",
			description = "콘서트 찾을 수 없음"
		)
	})
	@PostMapping("/{concertId}/queue")
	public ResponseEntity<QueueTokenResponse> issueQueueToken(
		@PathVariable UUID concertId,
		@RequestBody QueueTokenRequest request
	) {
		QueueTokenResponse response = new QueueTokenResponse("example-queue-token", 10, 5);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(
		summary = "콘서트 좌석 예약 API",
		description = "콘서트 좌석 예약"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "예약 성공",
			content = @Content(schema = @Schema(implementation = ReservationResponse.class))
		),
		@ApiResponse(
			responseCode = "404 - User",
			description = "유저 찾을 수 없음"
		),
		@ApiResponse(
			responseCode = "404 - Concert",
			description = "콘서트 찾을 수 없음"
		),
		@ApiResponse(
			responseCode = "404 - Seat",
			description = "좌석 찾을 수 없음"
		),
		@ApiResponse(
			responseCode = "400",
			description = "좌석이 AVAILABLE 상태가 아님"
		),
		@ApiResponse(
			responseCode = "401",
			description = "토큰 만료 등 토큰이 잘못됨"
		),
		@ApiResponse(
			responseCode = "409",
			description = "락 획득 실패 (다른 사용자 점유중)"
		)
	})
	@PostMapping("/{concertId}/reservations")
	public ResponseEntity<ReservationResponse> reservationConcert(
		@PathVariable UUID concertId,
		@RequestBody ReservationRequest request,
		@RequestHeader(value = "Authorization") String queueToken
	) {
		ReservationResponse response = new ReservationResponse(
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			10,
			BigDecimal.valueOf(50000),
			ReservationStatus.PENDING,
			LocalDateTime.now()
		);
		return ResponseEntity.ok(response);
	}

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
		List<ConcertDate> availableConcertDates = concertService.getAvailableConcertDates(concertId);
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
		List<Seat> availableSeats = concertService.getAvailableSeats(concertId, concertDateId);
		List<SeatResponse> response = availableSeats.stream()
			.map(SeatResponse::from)
			.toList();

		return ResponseEntity.ok(response);
	}
}
