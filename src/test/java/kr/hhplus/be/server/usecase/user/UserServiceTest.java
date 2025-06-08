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

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	private UUID userId;
	private BigDecimal initAmount;
	private User user;

	@BeforeEach
	void beforeEach() {
		userId = UUID.randomUUID();
		initAmount = BigDecimal.valueOf(10000);
		user = User.builder()
			.id(userId)
			.amount(initAmount)
			.build();
	}

	@Test
	@DisplayName("유저_조회_성공")
	void getUser_Success() throws CustomException {
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		User findUser = userService.getUser(userId);

		verify(userRepository, times(1)).findById(userId);
		assertThat(findUser).isNotNull();
		assertThat(findUser.id()).isEqualTo(userId);
		assertThat(findUser.amount()).isEqualTo(initAmount);
	}

	@Test
	@DisplayName("유저_조회_실패_유저못찾음")
	void getUser_Failure_UserNotFound() throws CustomException {
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> userService.getUser(userId));

		verify(userRepository, times(1)).findById(userId);
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("유저_포인트_충전_성공")
	void chargePoint_Success() throws CustomException {
		BigDecimal chargePoint = BigDecimal.valueOf(5000);
		User charged = user.charge(chargePoint);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenReturn(charged);

		User user = userService.chargePoint(userId, chargePoint);

		verify(userRepository, times(1)).findById(userId);

		assertThat(user).isNotNull();
		assertThat(user.amount()).isEqualTo(initAmount.add(chargePoint));
	}

	@Test
	@DisplayName("유저_포인트_충전_실패_유저못찾음")
	void chargePoint_Failure_UserNotFound() throws CustomException {
		BigDecimal chargePoint = BigDecimal.valueOf(5000);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> userService.chargePoint(userId, chargePoint));

		verify(userRepository, times(1)).findById(userId);

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("유저_포인트_충전_실패_최소충전금액미만(1000원)")
	void chargePoint_Failure_NotEnoughMinChargePoint() {
		BigDecimal chargePoint = BigDecimal.valueOf(500);

		CustomException customException = assertThrows(CustomException.class,
			() -> userService.chargePoint(userId, chargePoint));

		verify(userRepository, never()).findById(userId);

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.NOT_ENOUGH_MIN_CHARGE_POINT);
	}
}