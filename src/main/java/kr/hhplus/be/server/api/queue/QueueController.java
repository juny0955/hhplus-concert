package kr.hhplus.be.server.api.queue;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.api.queue.dto.response.QueueTokenResponse;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.usecase.queue.QueueService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
@Tag(name = "Queue API", description = "대기열 관련 API")
public class QueueController {

	private final QueueService queueService;

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
	@PostMapping("/concerts/{concertId}/users/{userId}")
	public ResponseEntity<QueueTokenResponse> issueQueueToken(
		@PathVariable UUID concertId,
		@PathVariable UUID userId
	) throws CustomException {
		QueueToken queueToken = queueService.issueQueueToken(userId, concertId);

		return ResponseEntity.status(HttpStatus.CREATED).body(QueueTokenResponse.from(queueToken));
	}

	@GetMapping("/concerts/{concertId}")
	public ResponseEntity<QueueTokenResponse> getQueueInfo(
		@PathVariable UUID concertId,
		@RequestHeader(value = "Authorization") String queueToken
	) throws CustomException {
		QueueToken result = queueService.getQueueInfo(concertId, queueToken);

		return ResponseEntity.ok(QueueTokenResponse.from(result));
	}
}
