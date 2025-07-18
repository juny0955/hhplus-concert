package kr.hhplus.be.server.payment.usecase;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.CompletePaymentEvent;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentEvent;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;
import kr.hhplus.be.server.payment.port.in.CancelPaymentUseCase;
import kr.hhplus.be.server.payment.port.in.CompletePaymentUseCase;
import kr.hhplus.be.server.payment.port.in.CreatePaymentUseCase;
import kr.hhplus.be.server.payment.port.in.PaymentCommand;
import kr.hhplus.be.server.payment.port.in.PaymentUseCase;
import kr.hhplus.be.server.payment.port.out.EventPublishPort;
import kr.hhplus.be.server.payment.port.out.GetPaymentPort;
import kr.hhplus.be.server.payment.port.out.QueueTokenQueryPort;
import kr.hhplus.be.server.payment.port.out.SavePaymentPort;
import kr.hhplus.be.server.payment.port.out.SeatHoldQueryPort;
import kr.hhplus.be.server.queue.domain.QueueToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService implements
		PaymentUseCase,
		CreatePaymentUseCase,
		CancelPaymentUseCase,
		CompletePaymentUseCase {

	private final GetPaymentPort getPaymentPort;
	private final SavePaymentPort savePaymentPort;
	private final QueueTokenQueryPort queueTokenQueryPort;
	private final SeatHoldQueryPort seatHoldQueryPort;
	private final EventPublishPort eventPublishPort;

	@Override
	@DistributedLock(key = "payment:reservation:#command.reservationId()")
	@Transactional
	public Payment pay(PaymentCommand command) throws Exception {
		QueueToken queueToken = queueTokenQueryPort.getActiveToken(command.queueTokenId());
		seatHoldQueryPort.hasSeatHold(command.seatId(), queueToken.userId());

		Payment payment = getPaymentPort.getPaymentByReservationId(command.reservationId());

		eventPublishPort.publishPaymentEvent(PaymentEvent.of(command, payment, queueToken.userId(), queueToken.tokenId()));
		return payment;
	}

	@Override
	@DistributedLock(key = "payment:reservation:#event.reservationId()")
	@Transactional
	public void completePayment(CompletePaymentEvent event) throws CustomException {
		Payment payment = getPaymentPort.getPaymentByReservationId(event.reservationId());
		savePaymentPort.save(payment.success());

		eventPublishPort.publishPaymentSuccessEvent(PaymentSuccessEvent.from(event));
	}

	@Override
	@Transactional
	public Payment createPayment(UUID userId, UUID reservationId, BigDecimal price) {
		return savePaymentPort.save(Payment.of(userId, reservationId, price));
	}

	@Override
	public Payment cancelPayment(UUID reservationId) throws CustomException {
		Payment payment = getPaymentPort.getPaymentByReservationId(reservationId);
		return savePaymentPort.save(payment.cancel());
	}
}
