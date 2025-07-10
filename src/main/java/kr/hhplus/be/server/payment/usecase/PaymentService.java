package kr.hhplus.be.server.payment.usecase;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.queue.QueueToken;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;
import kr.hhplus.be.server.payment.port.in.CancelPaymentUseCase;
import kr.hhplus.be.server.payment.port.in.CreatePaymentUseCase;
import kr.hhplus.be.server.payment.port.in.PaymentCommand;
import kr.hhplus.be.server.payment.port.in.PaymentUseCase;
import kr.hhplus.be.server.payment.port.out.GetPaymentPort;
import kr.hhplus.be.server.payment.port.out.QueueTokenQueryPort;
import kr.hhplus.be.server.payment.port.out.ReservationQueryPort;
import kr.hhplus.be.server.payment.port.out.SavePaymentPort;
import kr.hhplus.be.server.payment.port.out.SeatHoldQueryPort;
import kr.hhplus.be.server.payment.port.out.SeatQueryPort;
import kr.hhplus.be.server.payment.port.out.UserQueryPort;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
		Seat savedSeat = seatQueryPort.paidSeat(savedReservation.seatId(), queueToken.tokenId());

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
