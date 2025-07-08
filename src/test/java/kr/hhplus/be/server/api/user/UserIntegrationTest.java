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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.api.TestDataFactory;
import kr.hhplus.be.server.adapters.in.web.user.request.ChargePointRequest;
import kr.hhplus.be.server.application.user.domain.User;
import kr.hhplus.be.server.exception.ErrorCode;

@SpringBootTest(properties = {
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
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

	@BeforeEach
	void setUp() {
		user = TestDataFactory.createUser();
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
			.andExpect(jsonPath("$.amount").value(user.amount()));
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
	void chargePointUserPoint_Success() throws Exception {
		BigDecimal chargePoint = BigDecimal.valueOf(5000);
		ChargePointRequest request = new ChargePointRequest(chargePoint);

		mockMvc.perform(post("/api/v1/users/{userId}/points", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.userId").value(userId.toString()))
			.andExpect(jsonPath("$.amount").value(user.amount().add(chargePoint)))
		;

		User findUser = userRepository.findById(userId).get();
		assertThat(findUser.amount()).isEqualTo(user.amount().add(chargePoint));
	}

	@Test
	@DisplayName("유저_포인트_충전_실패_유저못찾음")
	void chargePointUserPoint_Failure_UserNotFound() throws Exception {
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
	void chargeUserPoint_Failure_NotEnoughMinChargePointPoint() throws Exception {
		UUID otherUserId = UUID.randomUUID();
		BigDecimal chargePoint = BigDecimal.valueOf(500);
		ChargePointRequest request = new ChargePointRequest(chargePoint);

		mockMvc.perform(post("/api/v1/users/{userId}/points", otherUserId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(ErrorCode.NOT_ENOUGH_MIN_CHARGE_POINT.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.NOT_ENOUGH_MIN_CHARGE_POINT.getMessage()))
		;

		User findUser = userRepository.findById(userId).get();
		assertThat(findUser.amount()).isEqualTo(user.amount());
	}

	@Test
	@DisplayName("유저_포인트_충전_성공_충전금액경계값(1000원)")
	void chargeUserPoint_Success_ChargePointPoint_1000Won() throws Exception {
		BigDecimal chargePoint = BigDecimal.valueOf(1000);
		ChargePointRequest request = new ChargePointRequest(chargePoint);

		mockMvc.perform(post("/api/v1/users/{userId}/points", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.userId").value(userId.toString()))
			.andExpect(jsonPath("$.amount").value(user.amount().add(chargePoint)));

		User findUser = userRepository.findById(userId).get();
		assertThat(findUser.amount()).isEqualTo(user.amount().add(chargePoint));
	}

}