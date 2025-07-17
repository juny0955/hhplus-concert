package kr.hhplus.be.server.queue.adapter.out.internal;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.queue.port.out.UserQueryPort;
import kr.hhplus.be.server.user.port.in.ExistsUserUseCase;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueUserAdapter implements UserQueryPort {

	private final ExistsUserUseCase existsUserUseCase;

	@Override
	public void existsUser(UUID userId) throws CustomException {
		existsUserUseCase.existsUser(userId);
	}
}
