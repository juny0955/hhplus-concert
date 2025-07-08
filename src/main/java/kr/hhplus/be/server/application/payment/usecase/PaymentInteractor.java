package kr.hhplus.be.server.application.payment.usecase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.payment.dto.PaymentResult;
import kr.hhplus.be.server.application.payment.port.in.PaymentCommand;
import kr.hhplus.be.server.application.payment.port.in.PaymentInput;
import kr.hhplus.be.server.application.payment.service.PaymentService;
import kr.hhplus.be.server.application.queue.port.in.GetActiveQueueTokenInput;
import kr.hhplus.be.server.application.seatHold.port.in.CheckSeatHoldInput;
import kr.hhplus.be.server.config.aop.DistributedLock;
import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.queue.QueueToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentInteractor implements PaymentInput {

	private final PaymentService paymentService;
	private final GetActiveQueueTokenInput getActiveQueueTokenInput;
	private final CheckSeatHoldInput checkSeatHoldInput;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	@DistributedLock(key = "payment:reservation:#command.reservationId()")
	public PaymentResult payment(PaymentCommand command) throws Exception {
		QueueToken queueToken = getActiveQueueTokenInput.getActiveQueueToken(command.queueTokenId());
		checkSeatHoldInput.checkSeatHold(command.seatId(), queueToken.userId());

		PaymentResult paymentResult = paymentService.processPayment(command, queueToken);

		eventPublisher.publishEvent(PaymentSuccessEvent.from(paymentResult));
		return paymentResult;
	}
}
