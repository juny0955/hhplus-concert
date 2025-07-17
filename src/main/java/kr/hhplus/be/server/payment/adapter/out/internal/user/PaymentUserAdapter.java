package kr.hhplus.be.server.payment.adapter.out.internal.user;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.payment.port.out.UserQueryPort;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.port.in.UsePointUseCase;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentUserAdapter implements UserQueryPort {

	private final UsePointUseCase usePointUseCase;

	@Override
	public User usePoint(UUID userId, BigDecimal point) throws Exception {
		return usePointUseCase.usePoint(userId, point);
	}
}
