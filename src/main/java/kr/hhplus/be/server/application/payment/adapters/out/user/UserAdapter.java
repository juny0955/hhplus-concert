package kr.hhplus.be.server.application.payment.adapters.out.user;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.payment.port.out.UsePointPort;
import kr.hhplus.be.server.application.user.port.in.UsePointUseCase;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UsePointPort {

	private final UsePointUseCase usePointUseCase;

	@Override
	public User usePoint(UUID userId, BigDecimal point) throws Exception {
		return usePointUseCase.usePoint(userId, point);
	}
}
