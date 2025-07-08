package kr.hhplus.be.server.application.payment.usecase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.adapters.out.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.application.payment.dto.PaymentResult;
import kr.hhplus.be.server.application.payment.port.in.PaymentCommand;
import kr.hhplus.be.server.application.payment.port.in.PaymentInput;
import kr.hhplus.be.server.application.payment.service.PaymentService;
import kr.hhplus.be.server.application.queue.port.in.GetActiveQueueTokenInput;
import kr.hhplus.be.server.application.seatHold.port.in.CheckSeatHoldInput;
import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.queue.QueueToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentInteractor implements PaymentInput {

	private static final String RESERVATION_LOCK_KEY = "payment:reservation:";

	private final PaymentService paymentService;
	private final GetActiveQueueTokenInput getActiveQueueTokenInput;
	private final CheckSeatHoldInput checkSeatHoldInput;
	private final ApplicationEventPublisher eventPublisher;
	private final DistributedLockManager distributedLockManager;

	@Override
	public PaymentResult payment(PaymentCommand command) throws Exception {
		QueueToken queueToken = getActiveQueueTokenInput.getActiveQueueToken(command.queueTokenId());
		checkSeatHoldInput.checkSeatHold(command.seatId(), queueToken.userId());

		String reservationLockKey = RESERVATION_LOCK_KEY + command.reservationId();

		// payment:reservation:{reservationId} 락 획득 후 결제 트랜잭션 수행
		PaymentResult paymentResult = distributedLockManager.executeWithLockHasReturn(
			reservationLockKey,
			() -> paymentService.processPayment(command, queueToken)
		);

		eventPublisher.publishEvent(PaymentSuccessEvent.from(paymentResult));
		return paymentResult;
	}
}
