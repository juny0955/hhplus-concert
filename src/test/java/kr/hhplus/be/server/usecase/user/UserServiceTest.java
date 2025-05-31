package kr.hhplus.be.server.usecase.user;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.entity.user.User;
import kr.hhplus.be.server.interfaces.gateway.repository.user.UserEntity;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	private UUID userId;
	private BigDecimal initAmount;
	private UserEntity userEntity;

	@BeforeEach
	void beforeEach() {
		userId = UUID.randomUUID();
		initAmount = BigDecimal.valueOf(10000);
		userEntity = UserEntity.builder()
			.id(userId.toString())
			.amount(initAmount)
			.build();
	}

	@Test
	@DisplayName("유저_조회_성공")
	void getUser_Success() {
		when(userRepository.findById(userId.toString())).thenReturn(Optional.of(userEntity));

		User findUser = userService.getUser(userId);

		verify(userRepository, times(1)).findById(userId.toString());
		assertThat(findUser).isNotNull();
		assertThat(findUser.id()).isEqualTo(userId);
		assertThat(findUser.amount()).isEqualTo(initAmount);
	}

	@Test
	@DisplayName("유저_조회_실패_유저못찾음")
	void getUser_Failure_UserNotFound() {
		when(userRepository.findById(userId.toString())).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> userService.getUser(userId));

		verify(userRepository, times(1)).findById(userId.toString());
	}

	@Test
	@DisplayName("유저_포인트_충전_성공")
	void chargePoint_Success() {
		BigDecimal chargePoint = BigDecimal.valueOf(5000);

		when(userRepository.findById(userId.toString())).thenReturn(Optional.of(userEntity));

		User user = userService.chargePoint(userId, chargePoint);

		verify(userRepository, times(1)).findById(userId.toString());

		assertThat(user).isNotNull();
		assertThat(user.amount()).isEqualTo(initAmount.add(chargePoint));
	}

	@Test
	@DisplayName("유저_포인트_충전_실패_유저못찾음")
	void chargePoint_Failure_UserNotFound() {
		BigDecimal chargePoint = BigDecimal.valueOf(5000);

		when(userRepository.findById(userId.toString())).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> userService.chargePoint(userId, chargePoint));

		verify(userRepository, times(1)).findById(userId.toString());
	}

	@Test
	@DisplayName("유저_포인트_충전_실패_최소충전금액미만(1000원)")
	void chargePoint_Failure_NotEnoughMinChargePoint() {
		BigDecimal chargePoint = BigDecimal.valueOf(500);

		assertThrows(IllegalArgumentException.class, () -> userService.chargePoint(userId, chargePoint));

		verify(userRepository, never()).findById(userId.toString());
	}
}