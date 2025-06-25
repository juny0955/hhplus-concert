package kr.hhplus.be.server.usecase.payment.interactor;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.event.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.infrastructure.persistence.payment.PaymentManager;
import kr.hhplus.be.server.infrastructure.persistence.payment.PaymentTransactionResult;
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

	private final static String LOCK_KEY = "reservation:";

	private final PaymentManager paymentManager;
	private final PaymentOutput paymentOutput;
	private final EventPublisher eventPublisher;
	private final DistributedLockManager distributedLockManager;

	@Override
	public void payment(PaymentCommand command) throws Exception {
		String lockKey = LOCK_KEY + command.reservationId();

		// reservation:{reservationId} 락 획득 후 결제 트랜잭션 수행
		PaymentTransactionResult paymentTransactionResult = distributedLockManager.executeWithLockHasReturn(
			lockKey,
			() -> paymentManager.processPayment(command)
		);

		eventPublisher.publish(PaymentSuccessEvent.from(paymentTransactionResult));
		paymentOutput.ok(PaymentResult.from(paymentTransactionResult));
	}
}
