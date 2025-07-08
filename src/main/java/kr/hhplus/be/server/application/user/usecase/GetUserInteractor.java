package kr.hhplus.be.server.application.user.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.user.port.in.GetUserInput;
import kr.hhplus.be.server.application.user.service.UserService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetUserInteractor implements GetUserInput {

	private final UserService userService;

	@Override
	public User getUser(UUID userId) throws CustomException {
		return userService.getUser(userId);
	}
}
