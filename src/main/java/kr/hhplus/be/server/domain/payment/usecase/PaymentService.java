package kr.hhplus.be.server.domain.payment.usecase;

import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.payment.domain.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.payment.port.in.CancelPaymentUseCase;
import kr.hhplus.be.server.domain.payment.port.in.CreatePaymentUseCase;
import kr.hhplus.be.server.domain.payment.port.in.PaymentCommand;
import kr.hhplus.be.server.domain.payment.port.in.PaymentUseCase;
import kr.hhplus.be.server.domain.payment.port.out.*;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.concert.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService implements
		PaymentUseCase,
		CreatePaymentUseCase,
		CancelPaymentUseCase {

	private final GetPaymentPort getPaymentPort;
	private final SavePaymentPort savePaymentPort;
	private final ReservationQueryPort reservationQueryPort;
	private final UserQueryPort userQueryPort;
	private final SeatQueryPort seatQueryPort;
	private final QueueTokenQueryPort queueTokenQueryPort;
	private final SeatHoldQueryPort seatHoldQueryPort;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	@DistributedLock(key = "payment:reservation:#command.reservationId()")
	@Transactional
	public Payment pay(PaymentCommand command) throws Exception {
		QueueToken queueToken = queueTokenQueryPort.getActiveToken(command.queueTokenId());
		seatHoldQueryPort.hasSeatHold(command.seatId(), queueToken.userId());

		Payment payment = getPaymentPort.getPaymentByReservationId(command.reservationId());

		Payment savedPayment = savePaymentPort.save(payment.success());
		User savedUser = userQueryPort.usePoint(queueToken.userId(), payment.amount());
		Reservation savedReservation = reservationQueryPort.paidReservation(command.reservationId());
		Seat savedSeat = seatQueryPort.paidSeat(savedReservation.seatId());

		seatHoldQueryPort.releaseSeatHold(savedSeat.id(), savedUser.id());
		queueTokenQueryPort.expireQueueToken(queueToken.tokenId().toString());

		eventPublisher.publishEvent(PaymentSuccessEvent.from(savedPayment, savedSeat, savedReservation, savedUser));
		return payment;
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
