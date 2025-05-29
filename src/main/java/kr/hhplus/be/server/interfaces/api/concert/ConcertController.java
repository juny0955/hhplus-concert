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
import kr.hhplus.be.server.domain.concert.model.SeatClass;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.interfaces.dto.request.QueueTokenRequest;
import kr.hhplus.be.server.interfaces.dto.request.ReservationRequest;
import kr.hhplus.be.server.interfaces.dto.response.ConcertDateResponse;
import kr.hhplus.be.server.interfaces.dto.response.QueueTokenResponse;
import kr.hhplus.be.server.interfaces.dto.response.ReservationResponse;
import kr.hhplus.be.server.interfaces.dto.response.SeatResponse;

@RestController
@RequestMapping("/api/v1/concerts")
@Tag(name = "Concert API", description = "콘서트 관련 API")
public class ConcertController {

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
		summary = "콘서트 좌석 예약 API",
		description = "콘서트 좌석 예약"
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
	@GetMapping("/{concertId}/date")
	public ResponseEntity<List<ConcertDateResponse>> getAvailableDates(
		@PathVariable UUID concertId
	) {
		ConcertDateResponse response = new ConcertDateResponse(
			UUID.randomUUID(),
			LocalDateTime.now(),
			LocalDateTime.now(),
			50
		);
		return ResponseEntity.ok(List.of(response));
	}

	@Operation(
		summary = "콘서트 좌석 예약 API",
		description = "콘서트 좌석 예약"
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
	@GetMapping("/{concertId}/date/{concertDateId}/seat")
	public ResponseEntity<List<SeatResponse>> getAvailableSeats(
		@PathVariable UUID concertId,
		@PathVariable UUID concertDateId
	) {
		SeatResponse response = new SeatResponse(
			UUID.randomUUID(),
			1,
			BigDecimal.valueOf(50000),
			SeatClass.VIP,
			SeatStatus.AVAILABLE
		);
		return ResponseEntity.ok(List.of(response));
	}
}
