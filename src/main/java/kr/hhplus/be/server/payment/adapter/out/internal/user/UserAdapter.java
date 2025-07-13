package kr.hhplus.be.server.payment.adapter.out.internal.user;

import kr.hhplus.be.server.payment.port.out.UserQueryPort;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.port.in.UsePointUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserQueryPort {

	private final UsePointUseCase usePointUseCase;

	@Override
	public User usePoint(UUID userId, BigDecimal point) throws Exception {
		return usePointUseCase.usePoint(userId, point);
	}
}
