package kr.hhplus.be.server.concert.port.in.soldoutrank;

import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;

public interface UpdateSoldOutRankUseCase {
	void updateSoldOutRank(PaymentSuccessEvent event);
}
