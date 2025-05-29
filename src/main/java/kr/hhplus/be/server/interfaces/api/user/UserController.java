package kr.hhplus.be.server.interfaces.api.user;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
import kr.hhplus.be.server.interfaces.dto.request.ChargePointRequest;
import kr.hhplus.be.server.interfaces.dto.response.UserPointResponse;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "유저 관련 API")
public class UserController {

	@Operation(
		summary = "유저 포인트 조회",
		description = "{userId}에 해당하는 유저의 포인트 잔액 조회"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "조회 성공",
			content = @Content(schema = @Schema(implementation = UserPointResponse.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "유저 찾을 수 없음"
		)
	})
	@GetMapping("/{userId}/points")
	public ResponseEntity<UserPointResponse> getPoint(
		@PathVariable String userId
	) {
		UserPointResponse response = new UserPointResponse(UUID.randomUUID(), BigDecimal.valueOf(10000));
		return ResponseEntity.ok(response);
	}

	@Operation(
		summary = "유저 포인트 충전",
		description = "{userId}에 해당하는 유저의 포인트 충전"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "충전 성공",
			content = @Content(schema = @Schema(implementation = UserPointResponse.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "유저 찾을 수 없음"
		),
		@ApiResponse(
			responseCode = "400",
			description = "음수값 충전 등 잘못된 요청"
		)
	})
	@PostMapping("/{userId}/points")
	public ResponseEntity<UserPointResponse> chargePoint(
		@PathVariable UUID userId,
		@RequestBody ChargePointRequest request
	) {
		UserPointResponse response = new UserPointResponse(UUID.randomUUID(), BigDecimal.valueOf(10000).add(request.point()));
		return ResponseEntity.ok(response);
	}
}
