package kr.hhplus.be.server.application.payment.usecase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.payment.dto.PaymentResult;
import kr.hhplus.be.server.application.payment.port.in.PaymentCommand;
import kr.hhplus.be.server.application.payment.port.in.PaymentUseCase;
import kr.hhplus.be.server.application.payment.port.out.CheckSeatHoldPort;
import kr.hhplus.be.server.application.payment.port.out.ExpireQueueTokenPort;
import kr.hhplus.be.server.application.payment.port.out.GetActiveTokenPort;
import kr.hhplus.be.server.application.payment.port.out.GetPaymentPort;
import kr.hhplus.be.server.application.payment.port.out.PaidReservationPort;
import kr.hhplus.be.server.application.payment.port.out.PaidSeatPort;
import kr.hhplus.be.server.application.payment.port.out.ReleaseSeatHoldPort;
import kr.hhplus.be.server.application.payment.port.out.SavePaymentPort;
import kr.hhplus.be.server.application.payment.port.out.UsePointPort;
import kr.hhplus.be.server.config.aop.DistributedLock;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentInteractor implements PaymentUseCase {

	private final GetPaymentPort getPaymentPort;
	private final SavePaymentPort savePaymentPort;
	private final PaidReservationPort paidReservation;
	private final UsePointPort usePointPort;
	private final PaidSeatPort paidSeatPort;
	private final GetActiveTokenPort getActiveTokenPort;
	private final CheckSeatHoldPort checkSeatHoldPort;
	private final ReleaseSeatHoldPort releaseSeatHoldPort;
	private final ExpireQueueTokenPort expireQueueTokenPort;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	@DistributedLock(key = "payment:reservation:#command.reservationId()")
	@Transactional
	public PaymentResult payment(PaymentCommand command) throws Exception {
		QueueToken queueToken = getActiveTokenPort.getActiveToken(command.queueTokenId());
		checkSeatHoldPort.checkSeatHold(command.seatId(), queueToken.userId());

		Payment payment = getPaymentPort.getPaymentByReservationId(command.reservationId());

		Payment savedPayment		 = savePaymentPort.save(payment.success());
		User savedUser          	 = usePointPort.usePoint(queueToken.userId(), payment.amount());
		Reservation savedReservation = paidReservation.paidReservation(command.reservationId());
		Seat savedSeat       	 	 = paidSeatPort.paidSeat(savedReservation.seatId());

		releaseSeatHoldPort.releaseSeatHold(savedSeat.id(), savedUser.id());
		expireQueueTokenPort.expireQueueToken(queueToken.tokenId().toString());

		eventPublisher.publishEvent(PaymentSuccessEvent.from(savedPayment, savedSeat, savedReservation, savedUser));
		return new PaymentResult(savedPayment, savedSeat, savedReservation, savedUser);
	}
}
