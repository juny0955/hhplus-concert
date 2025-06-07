package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.event.KafkaEventObject;
import kr.hhplus.be.server.domain.event.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.exception.CustomException;
import kr.hhplus.be.server.usecase.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentDomainService {

	private final EventPublisher eventPublisher;

	public PaymentDomainResult processPayment(Reservation reservation, Payment payment, Seat seat, User user) throws CustomException {
		validateUserBalance(payment, user);

		User paidUser = user.payment(payment.amount());
		Reservation paidReservation = reservation.payment();
		Payment paidPayment = payment.success();
		Seat paidSeat = seat.payment();

		return new PaymentDomainResult(paidUser, paidReservation, paidPayment, paidSeat);
	}

	public void handlePaymentSuccess(Payment payment, Reservation reservation, Seat seat, User user) {
		PaymentSuccessEvent paymentSuccessEvent = PaymentSuccessEvent.of(payment.id(), reservation.id(), seat.id(), user.id(), payment.amount());
		KafkaEventObject<PaymentSuccessEvent> kafkaEventObject = KafkaEventObject.from(paymentSuccessEvent);
		eventPublisher.publish(kafkaEventObject);
	}

	private void validateUserBalance(Payment payment, User user) throws CustomException {
		if (!user.checkEnoughAmount(payment.amount()))
			throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
	}
}
