package kr.hhplus.be.server.user.application.interactor;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.user.application.service.UserApplicationService;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.ports.in.GetUserInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetUserInteractor implements GetUserInput {

	private final UserApplicationService userApplicationService;

	@Override
	public User getUser(UUID userId) throws CustomException {
		return userApplicationService.getUser(userId);
	}
}
