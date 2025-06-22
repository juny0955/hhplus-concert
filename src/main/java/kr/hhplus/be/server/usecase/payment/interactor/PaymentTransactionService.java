package kr.hhplus.be.server.usecase.payment.interactor;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.usecase.payment.input.PaymentCommand;

public interface PaymentTransactionService {
	PaymentTransactionResult processPaymentTransaction(PaymentCommand command) throws CustomException;
}

