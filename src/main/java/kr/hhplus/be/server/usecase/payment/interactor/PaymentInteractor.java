package kr.hhplus.be.server.usecase.payment.interactor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.usecase.payment.input.PaymentCommand;
import kr.hhplus.be.server.usecase.payment.input.PaymentInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentInteractor implements PaymentInput {

	@Override
	public void payment(PaymentCommand commend) {

	}
}
