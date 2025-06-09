package kr.hhplus.be.server.api.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.api.user.dto.request.ChargePointRequest;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.framework.exception.ErrorCode;

@SpringBootTest(properties = {
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private UUID userId;
	private User user;
	private BigDecimal initPoint = BigDecimal.valueOf(10000);

	@BeforeEach
	void setUp() {
		user = User.builder()
			.amount(initPoint)
			.build();
		User save = userRepository.save(user);
		userId = save.id();
	}

	@Test
	@DisplayName("유저_포인트_조회_성공")
	void getUserPoint_Success() throws Exception {
		mockMvc.perform(get("/api/v1/users/{userId}/points", userId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value(userId.toString()))
			.andExpect(jsonPath("$.amount").value(initPoint));
	}

	@Test
	@DisplayName("유저_포인트_조회_실패_유저못찾음")
	void getUserPoint_Failure_UserNotFound() throws Exception {
		UUID userId = UUID.randomUUID();

		mockMvc.perform(get("/api/v1/users/{userId}/points", userId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
		;
	}

	@Test
	@DisplayName("유저_포인트_충전_성공")
	void chargeUserPoint_Success() throws Exception {
		BigDecimal chargePoint = BigDecimal.valueOf(5000);
		ChargePointRequest request = new ChargePointRequest(chargePoint);

		mockMvc.perform(post("/api/v1/users/{userId}/points", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.userId").value(userId.toString()))
			.andExpect(jsonPath("$.amount").value(initPoint.add(chargePoint)))
		;
	}

	@Test
	@DisplayName("유저_포인트_충전_실패_유저못찾음")
	void chargeUserPoint_Failure_UserNotFound() throws Exception {
		UUID userId = UUID.randomUUID();
		BigDecimal chargePoint = BigDecimal.valueOf(5000);
		ChargePointRequest request = new ChargePointRequest(chargePoint);

		mockMvc.perform(post("/api/v1/users/{userId}/points", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
		;
	}

	@Test
	@DisplayName("유저_포인트_충전_실패_최소충전금액미만(1000원)")
	void chargeUserPoint_Failure_NotEnoughMinChargePoint() throws Exception {
		UUID userId = UUID.randomUUID();
		BigDecimal chargePoint = BigDecimal.valueOf(500);
		ChargePointRequest request = new ChargePointRequest(chargePoint);

		mockMvc.perform(post("/api/v1/users/{userId}/points", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(ErrorCode.NOT_ENOUGH_MIN_CHARGE_POINT.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.NOT_ENOUGH_MIN_CHARGE_POINT.getMessage()))
		;
	}

	@Test
	@DisplayName("유저_포인트_충전_성공_충전금액경계값(1000원)")
	void chargeUserPoint_Success_ChargePoint_1000Won() throws Exception {
		BigDecimal chargePoint = BigDecimal.valueOf(1000);
		ChargePointRequest request = new ChargePointRequest(chargePoint);

		mockMvc.perform(post("/api/v1/users/{userId}/points", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.userId").value(userId.toString()))
			.andExpect(jsonPath("$.amount").value(initPoint.add(chargePoint)));
	}

}