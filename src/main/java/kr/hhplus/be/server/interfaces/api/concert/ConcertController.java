package kr.hhplus.be.server.interfaces.api.concert;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.dto.request.QueueTokenRequest;
import kr.hhplus.be.server.interfaces.dto.response.QueueTokenResponse;

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
}
