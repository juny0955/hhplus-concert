package kr.hhplus.be.server.usecase.user;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.aop.DistributedLockAspect;
import kr.hhplus.be.server.user.application.service.UserManager;
import kr.hhplus.be.server.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserManager userManager;

	@Mock
	private DistributedLockAspect distributedLockAspect;

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
		when(userManager.getUser(userId)).thenReturn(user);

		User findUser = userService.getUser(userId);

		verify(userManager, times(1)).getUser(userId);
		assertThat(findUser).isNotNull();
		assertThat(findUser.id()).isEqualTo(userId);
		assertThat(findUser.amount()).isEqualTo(initAmount);
	}

	@Test
	@DisplayName("유저_조회_실패_유저못찾음")
	void getUser_Failure_UserNotFound() throws CustomException {
		when(userManager.getUser(userId)).thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		CustomException customException = assertThrows(CustomException.class,
			() -> userService.getUser(userId));

		verify(userManager, times(1)).getUser(userId);
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("유저_포인트_충전_성공")
	void chargePointPoint_Success() throws Exception {
		BigDecimal chargePoint = BigDecimal.valueOf(5000);
		User charged = user.chargePoint(chargePoint);
		String expectedLockKey = "user:" + userId;

		// 분산락 Mock 설정
		when(distributedLockAspect.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<User> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(userManager.chargePoint(userId, chargePoint)).thenReturn(charged);

		User result = userService.chargePoint(userId, chargePoint);

		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(userManager, times(1)).chargePoint(userId, chargePoint);
		assertThat(result).isNotNull();
		assertThat(result.amount()).isEqualTo(initAmount.add(chargePoint));
	}

	@Test
	@DisplayName("유저_포인트_충전_실패_유저못찾음")
	void chargePointPoint_Failure_UserNotFound() throws Exception {
		BigDecimal chargePoint = BigDecimal.valueOf(5000);
		String expectedLockKey = "user:" + userId;

		when(distributedLockAspect.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<User> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(userManager.chargePoint(userId, chargePoint)).thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		CustomException customException = assertThrows(CustomException.class,
			() -> userService.chargePoint(userId, chargePoint));

		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(userManager, times(1)).chargePoint(userId, chargePoint);
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("유저_포인트_충전_실패_최소충전금액미만(1000원)")
	void chargePoint_Failure_NotEnoughMinChargePointPoint() throws Exception {
		BigDecimal chargePoint = BigDecimal.valueOf(500);

		CustomException customException = assertThrows(CustomException.class,
			() -> userService.chargePoint(userId, chargePoint));

		verify(distributedLockAspect, never()).executeWithLockHasReturn(anyString(), any());
		verify(userManager, never()).chargePoint(userId, chargePoint);

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.NOT_ENOUGH_MIN_CHARGE_POINT);
	}
}