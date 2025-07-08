package kr.hhplus.be.server.application.user.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.user.port.in.FindUserUseCase;
import kr.hhplus.be.server.application.user.port.out.GetUserPort;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FindUserInteractor implements FindUserUseCase {

	private final GetUserPort getUserPort;

	@Override
	public User find(UUID userId) throws CustomException {
		return getUserPort.getUser(userId);
	}
}
