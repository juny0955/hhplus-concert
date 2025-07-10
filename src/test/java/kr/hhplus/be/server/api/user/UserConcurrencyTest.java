package kr.hhplus.be.server.api.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.api.TestDataFactory;
import kr.hhplus.be.server.user.adapter.in.web.request.ChargePointRequest;
import kr.hhplus.be.server.user.domain.User;

@SpringBootTest(properties = {
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public class UserConcurrencyTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private static final int THREAD_SIZE = 3;

	private UUID userId;
	private User user;

	@BeforeEach
	void setUp() {
		cleanUp();

		user = TestDataFactory.createUser();
		User save = userRepository.save(user);
		userId = save.id();
	}

	private void cleanUp() {
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("유저_포인트_동시충전")
	@Rollback
	void chargePointPoint_concurrency_test() throws Exception {
		BigDecimal chargePoint = BigDecimal.valueOf(5000);
		ChargePointRequest request = new ChargePointRequest(chargePoint);

		List<CompletableFuture<Void>> futures = new ArrayList<>();

		for (int i = 0; i < THREAD_SIZE; i++) {
			futures.add(CompletableFuture.runAsync(() -> {
				try {
					mockMvc.perform(post("/api/v1/users/{userId}/points", userId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
						.andExpect(status().isOk());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}));
		}

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(10, TimeUnit.SECONDS);

		User chargedUser = userRepository.findById(userId).get();
		assertThat(chargedUser.amount()).isEqualTo(user.amount().add(chargePoint.multiply(BigDecimal.valueOf(THREAD_SIZE))));
	}

}
