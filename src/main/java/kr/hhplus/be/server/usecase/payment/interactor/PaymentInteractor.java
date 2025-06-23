package kr.hhplus.be.server.usecase.payment.interactor;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.event.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
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

	private final PaymentManager paymentManager;
	private final PaymentOutput paymentOutput;
	private final EventPublisher eventPublisher;

	@Override
	public void payment(PaymentCommand command) throws CustomException {
		try {
			PaymentTransactionResult paymentTransactionResult = paymentManager.processPayment(command);

			eventPublisher.publish(PaymentSuccessEvent.from(paymentTransactionResult));
			paymentOutput.ok(PaymentResult.from(paymentTransactionResult));
		} catch (CustomException e) {
			log.warn("결제 진행 중 비즈니스 예외 발생 - {}", e.getErrorCode().name());
			throw e;
		} catch (Exception e) {
			log.error("결제 진행 중 예외 발생 - {}", ErrorCode.INTERNAL_SERVER_ERROR, e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
