package kr.hhplus.be.server.api.queue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.concertDate.ConcertDateRepository;
import kr.hhplus.be.server.domain.queue.QueueStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.framework.exception.ErrorCode;

@SpringBootTest(properties = {
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
class QueueIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ConcertRepository concertRepository;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ConcertDateRepository concertDateRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private UUID concertId;
	private UUID userId;
	private UUID concertDateId;

	private User user;
	private Concert concert;
	private ConcertDate concertDate;

	@BeforeEach
	void beforeEach() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();

		concert = Concert.builder()
			.title("GD 콘서트")
			.artist("GD")
			.build();
		Concert savedConcert = concertRepository.save(concert);
		concertId = savedConcert.id();

		concertDate = ConcertDate.builder()
			.concertId(concertId)
			.date(LocalDateTime.now().plusDays(7))
			.deadline(LocalDateTime.now().plusDays(5))
			.build();
		ConcertDate savedConcertDate = concertDateRepository.save(concertDate);
		concertDateId = savedConcertDate.id();

		user = User.builder()
			.amount(BigDecimal.valueOf(10000))
			.build();
		User savedUser = userRepository.save(user);
		userId = savedUser.id();
	}

	@Test
	@DisplayName("대기열_토큰_발급_성공(활성상태)")
	void issueQueueToken_Success_Active() throws Exception {
		mockMvc.perform(post("/api/v1/queue/concerts/{concertId}/users/{userId}", concertId, userId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.tokenId").exists())
			.andExpect(jsonPath("$.userId").value(userId.toString()))
			.andExpect(jsonPath("$.concertId").value(concertId.toString()))
			.andExpect(jsonPath("$.status").value(QueueStatus.ACTIVE.toString()))
			.andExpect(jsonPath("$.position").value(0))
			.andExpect(jsonPath("$.issuedAt").exists())
			.andExpect(jsonPath("$.expiresAt").exists())
			.andExpect(jsonPath("$.enteredAt").exists())
			.andExpect(jsonPath("$.waitTime").value(0));
			;
	}

	@Test
	@DisplayName("대기열_토큰_발급_성공(대기상태)")
	void issueQueueToken_Success_Waiting() throws Exception {
		for (int i = 0; i < 50; i++) {
			User tempUser = User.builder()
				.amount(BigDecimal.valueOf(10000))
				.build();
			User savedTempUser = userRepository.save(tempUser);

			mockMvc.perform(post("/api/v1/queue/concerts/{concertId}/users/{userId}", concertId, savedTempUser.id())
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		}

		// 51번째 토큰은 대기 상태
		mockMvc.perform(post("/api/v1/queue/concerts/{concertId}/users/{userId}", concertId, userId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.tokenId").exists())
			.andExpect(jsonPath("$.userId").value(userId.toString()))
			.andExpect(jsonPath("$.concertId").value(concertId.toString()))
			.andExpect(jsonPath("$.status").value(QueueStatus.WAITING.toString()))
			.andExpect(jsonPath("$.position").value(1))
			.andExpect(jsonPath("$.issuedAt").exists())
			.andExpect(jsonPath("$.expiresAt").doesNotExist())
			.andExpect(jsonPath("$.enteredAt").doesNotExist())
			.andExpect(jsonPath("$.waitTime").value(3));
		;
	}

	@Test
	@DisplayName("대기열_토큰_발급_성공(기존토큰반환)")
	void issueQueueToken_Success_ReturnExistingToken() throws Exception {
		MvcResult firstResult = mockMvc.perform(post("/api/v1/queue/concerts/{concertId}/users/{userId}", concertId, userId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andReturn();

		JsonNode firstResponse = objectMapper.readTree(firstResult.getResponse().getContentAsString());
		String firstTokenId = firstResponse.get("tokenId").asText();

		MvcResult secondResult = mockMvc.perform(post("/api/v1/queue/concerts/{concertId}/users/{userId}", concertId, userId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andReturn();

		JsonNode secondResponse = objectMapper.readTree(secondResult.getResponse().getContentAsString());
		String secondTokenId = secondResponse.get("tokenId").asText();

		assert firstTokenId.equals(secondTokenId);
	}

	@Test
	@DisplayName("대기열_토큰_발급_실패_유저못찾음")
	void issueQueueToken_Failure_UserNotFound() throws Exception {
		UUID userId = UUID.randomUUID();

		mockMvc.perform(post("/api/v1/queue/concerts/{concertId}/users/{userId}", concertId, userId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
		;
	}

	@Test
	@DisplayName("대기열_토큰_발급_실패_콘서트못찾음")
	void issueQueueToken_Failure_ConcertNotFound() throws Exception {
		UUID concertId = UUID.randomUUID();

		mockMvc.perform(post("/api/v1/queue/concerts/{concertId}/users/{userId}", concertId, userId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorCode.CONCERT_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.CONCERT_NOT_FOUND.getMessage()))
		;
	}

	@Test
	@DisplayName("대기열_토큰_조회_성공(활성상태)")
	void getQueueToken_Success_Active() throws Exception {
		MvcResult issueResult = mockMvc.perform(post("/api/v1/queue/concerts/{concertId}/users/{userId}", concertId, userId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andReturn();

		JsonNode issueResponse = objectMapper.readTree(issueResult.getResponse().getContentAsString());
		String tokenId = issueResponse.get("tokenId").asText();

		// 대기열 정보 조회
		mockMvc.perform(get("/api/v1/queue/concerts/{concertId}", concertId)
				.header("Authorization", tokenId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.tokenId").value(tokenId))
			.andExpect(jsonPath("$.userId").value(userId.toString()))
			.andExpect(jsonPath("$.concertId").value(concertId.toString()))
			.andExpect(jsonPath("$.status").value(QueueStatus.ACTIVE.toString()));
	}

	@Test
	@DisplayName("대기열_정보_조회_성공(대기상태)")
	void getQueueInfo_Success_Waiting() throws Exception {
		for (int i = 0; i < 50; i++) {
			User tempUser = User.builder()
				.amount(BigDecimal.valueOf(10000))
				.build();
			User savedTempUser = userRepository.save(tempUser);

			mockMvc.perform(post("/api/v1/queue/concerts/{concertId}/users/{userId}", concertId, savedTempUser.id())
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		}

		// 대기 토큰 발급
		MvcResult issueResult = mockMvc.perform(post("/api/v1/queue/concerts/{concertId}/users/{userId}", concertId, userId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andReturn();

		JsonNode issueResponse = objectMapper.readTree(issueResult.getResponse().getContentAsString());
		String tokenId = issueResponse.get("tokenId").asText();

		// 대기열 정보 조회
		mockMvc.perform(get("/api/v1/queue/concerts/{concertId}", concertId)
				.header("Authorization", tokenId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.tokenId").value(tokenId))
			.andExpect(jsonPath("$.userId").value(userId.toString()))
			.andExpect(jsonPath("$.concertId").value(concertId.toString()))
			.andExpect(jsonPath("$.status").value(QueueStatus.WAITING.toString()))
			.andExpect(jsonPath("$.position").value(1));
	}

	@Test
	@DisplayName("대기열_정보_조회_실패_유효하지않은토큰")
	void getQueueInfo_Failure_InvalidToken() throws Exception {
		String invalidTokenId = UUID.randomUUID().toString();

		mockMvc.perform(get("/api/v1/queue/concerts/{concertId}", concertId)
				.header("Authorization", invalidTokenId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(ErrorCode.INVALID_QUEUE_TOKEN.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.INVALID_QUEUE_TOKEN.getMessage()))
		;
	}

	@Test
	@DisplayName("대기열_정보_조회_실패_콘서트찾을수없음")
	void getQueueInfo_Failure_ConcertNotFound() throws Exception {
		// 토큰 발급
		MvcResult issueResult = mockMvc.perform(post("/api/v1/queue/concerts/{concertId}/users/{userId}", concertId, userId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andReturn();

		JsonNode issueResponse = objectMapper.readTree(issueResult.getResponse().getContentAsString());
		String tokenId = issueResponse.get("tokenId").asText();

		UUID nonExistentConcertId = UUID.randomUUID();

		mockMvc.perform(get("/api/v1/queue/concerts/{concertId}", nonExistentConcertId)
				.header("Authorization", tokenId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorCode.CONCERT_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorCode.CONCERT_NOT_FOUND.getMessage()))
		;
	}
}