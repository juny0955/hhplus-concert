package kr.hhplus.be.server.payment.application.interactor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.payment.application.dto.PaymentResult;
import kr.hhplus.be.server.payment.application.service.PaymentApplicationService;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;
import kr.hhplus.be.server.payment.domain.PaymentTransactionResult;
import kr.hhplus.be.server.payment.ports.in.PaymentCommand;
import kr.hhplus.be.server.payment.ports.in.PaymentInput;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.ports.in.GetActiveQueueTokenInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentInteractor implements PaymentInput {

	private static final String RESERVATION_LOCK_KEY = "payment:reservation:";

	private final PaymentApplicationService paymentApplicationService;
	private final GetActiveQueueTokenInput getActiveQueueTokenInput;
	private final ApplicationEventPublisher eventPublisher;
	private final DistributedLockManager distributedLockManager;

	@Override
	public PaymentResult payment(PaymentCommand command) throws Exception {
		QueueToken queueToken = getActiveQueueTokenInput.getActiveQueueToken(command.queueTokenId());
		String reservationLockKey = RESERVATION_LOCK_KEY + command.reservationId();

		// payment:reservation:{reservationId} 락 획득 후 결제 트랜잭션 수행
		PaymentTransactionResult paymentTransactionResult = distributedLockManager.executeWithLockHasReturn(
			reservationLockKey,
			() -> paymentApplicationService.processPayment(command, queueToken)
		);

		eventPublisher.publishEvent(PaymentSuccessEvent.from(paymentTransactionResult));
		return PaymentResult.from(paymentTransactionResult);
	}
}
