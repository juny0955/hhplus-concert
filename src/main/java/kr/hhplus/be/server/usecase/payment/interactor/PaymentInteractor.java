package kr.hhplus.be.server.usecase.payment.interactor;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.event.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.queue.QueueTokenUtil;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.infrastructure.persistence.payment.PaymentManager;
import kr.hhplus.be.server.infrastructure.persistence.payment.PaymentTransactionResult;
import kr.hhplus.be.server.infrastructure.persistence.queue.QueueTokenManager;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.payment.input.PaymentCommand;
import kr.hhplus.be.server.usecase.payment.input.PaymentInput;
import kr.hhplus.be.server.usecase.payment.output.PaymentOutput;
import kr.hhplus.be.server.usecase.payment.output.PaymentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentInteractor implements PaymentInput {

	private static final String RESERVATION_LOCK_KEY = "reservation:";
	private static final String USER_LOCK_KEY = "user:";

	private final PaymentManager paymentManager;
	private final QueueTokenManager queueTokenManager;
	private final PaymentOutput paymentOutput;
	private final EventPublisher eventPublisher;
	private final DistributedLockManager distributedLockManager;

	@Override
	public void payment(PaymentCommand command) throws Exception {
		QueueToken queueToken = getQueueTokenAndValid(command.queueTokenId());
		String reservationLockKey = RESERVATION_LOCK_KEY + command.reservationId();
		String userLockKey = USER_LOCK_KEY + queueToken.userId();

		/*
		  1. user:{userId} 락 획득
		  2. reservation:{reservationId} 락 획득
		  3. 결제 트랜잭션 수행
 		 */
		PaymentTransactionResult paymentTransactionResult = distributedLockManager.executeWithLockHasReturn(
			userLockKey,
			() -> distributedLockManager.executeWithLockHasReturn(
				reservationLockKey,
				() -> paymentManager.processPayment(command, queueToken)
			)
		);

		eventPublisher.publish(PaymentSuccessEvent.from(paymentTransactionResult));
		paymentOutput.ok(PaymentResult.from(paymentTransactionResult));
	}

	private QueueToken getQueueTokenAndValid(String tokenId) throws CustomException {
		QueueToken queueToken = queueTokenManager.getQueueToken(tokenId);
		QueueTokenUtil.validateActiveQueueToken(queueToken);
		return queueToken;
	}
}
