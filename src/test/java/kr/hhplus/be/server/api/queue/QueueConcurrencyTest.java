package kr.hhplus.be.server.api.queue;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
import org.testcontainers.utility.TestcontainersConfiguration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.api.TestDataFactory;
import kr.hhplus.be.server.domain.concert.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.user.domain.User;

@SpringBootTest(properties = {
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public class QueueConcurrencyTest {

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

	private static final int THREAD_SIZE = 2;

	private UUID concertId;
	private UUID userId;
	private UUID concertDateId;

	private User user;
	private Concert concert;
	private ConcertDate concertDate;

	@BeforeEach
	void beforeEach() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();

		concert = TestDataFactory.createConcert();
		Concert savedConcert = concertRepository.save(concert);
		concertId = savedConcert.id();

		concertDate = TestDataFactory.createConcertDate(concertId);
		ConcertDate savedConcertDate = concertDateRepository.save(concertDate);
		concertDateId = savedConcertDate.id();

		user = TestDataFactory.createUser();
		User savedUser = userRepository.save(user);
		userId = savedUser.id();
	}

	@Test
	@DisplayName("대기열_토큰_동시발급")
	void issueQueueToken_Concurrency_Test() throws Exception {
		List<String> tokenIdList = Collections.synchronizedList(new ArrayList<>());
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		for (int i = 0; i <THREAD_SIZE; i++) {
			futures.add(CompletableFuture.runAsync(() -> {
				try {
					MvcResult mvcResult = mockMvc.perform(
							post("/api/v1/queue/concerts/{concertId}/users/{userId}", concertId, userId)
								.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andReturn();

					String responseContent = mvcResult.getResponse().getContentAsString();
					JsonNode jsonNode = objectMapper.readTree(responseContent);
					String tokenId = jsonNode.get("tokenId").asText();
					tokenIdList.add(tokenId);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}));
		}

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(10, TimeUnit.SECONDS);
		assertThat(tokenIdList.get(0)).isEqualTo(tokenIdList.get(1)); // 기존 토큰이 있을 시 기존 토큰 사용
	}
}
