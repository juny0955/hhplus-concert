package kr.hhplus.be.server.usecase.payment.interactor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.event.KafkaEventObject;
import kr.hhplus.be.server.domain.event.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.usecase.concert.SeatRepository;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.exception.CustomException;
import kr.hhplus.be.server.usecase.exception.ErrorCode;
import kr.hhplus.be.server.usecase.payment.PaymentRepository;
import kr.hhplus.be.server.usecase.payment.input.PaymentCommand;
import kr.hhplus.be.server.usecase.payment.input.PaymentInput;
import kr.hhplus.be.server.usecase.payment.output.PaymentOutput;
import kr.hhplus.be.server.usecase.payment.output.PaymentResult;
import kr.hhplus.be.server.usecase.queue.QueueTokenRepository;
import kr.hhplus.be.server.usecase.queue.QueueTokenUtil;
import kr.hhplus.be.server.usecase.reservation.ReservationRepository;
import kr.hhplus.be.server.usecase.reservation.SeatHoldRepository;
import kr.hhplus.be.server.usecase.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentInteractor implements PaymentInput {

	private final QueueTokenRepository queueTokenRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final ReservationRepository reservationRepository;
	private final UserRepository userRepository;
	private final SeatRepository seatRepository;
	private final PaymentRepository paymentRepository;
	private final PaymentOutput paymentOutput;
	private final EventPublisher eventPublisher;

	@Override
	@Transactional
	public void payment(PaymentCommand command) throws CustomException {
		try {
			QueueToken queueToken = getQueueTokenAndValid(command);

			Reservation reservation = getReservation(command);
			Payment payment = getPayment(reservation);
			Seat seat = getSeat(reservation);
			User user = getUserAndValid(queueToken, payment);

			validateSeatHold(seat, user);

			User savedUser = userRepository.save(user.payment(payment.amount()));
			Reservation savedReservation = reservationRepository.save(reservation.payment());
			Payment savedPayment = paymentRepository.save(payment.success());
			Seat savedSeat = seatRepository.save(seat.payment());

			afterSuccessProcess(savedSeat, savedUser, queueToken);

			paymentOutput.ok(PaymentResult.of(savedPayment, savedSeat, savedReservation.id(), savedUser.id()));

			publishPaymentSuccessEvent(savedPayment, savedReservation, savedSeat, savedUser);
		} catch (CustomException e) {
			log.warn("결제 진행 중 비즈니스 예외 발생 - {}", e.getErrorCode(), e);
			throw e;
		} catch (Exception e) {
			log.error("결제 진행 중 예외 발생 - {}", ErrorCode.INTERNAL_SERVER_ERROR, e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	private void afterSuccessProcess(Seat savedSeat, User savedUser, QueueToken queueToken) {
		seatHoldRepository.deleteHold(savedSeat.id(), savedUser.id());
		queueTokenRepository.expiresQueueToken(queueToken.tokenId().toString());
	}

	private void validateSeatHold(Seat seat, User user) throws CustomException {
		if (!seatHoldRepository.isHoldSeat(seat.id(), user.id()))
			throw new CustomException(ErrorCode.SEAT_NOT_HOLD);
	}

	private void publishPaymentSuccessEvent(Payment payment, Reservation reservation, Seat seat, User user) {
		PaymentSuccessEvent paymentSuccessEvent = PaymentSuccessEvent.of(
			payment.id(), reservation.id(), seat.id(), user.id(), payment.amount());
		KafkaEventObject<PaymentSuccessEvent> kafkaEventObject = KafkaEventObject.from(paymentSuccessEvent);
		eventPublisher.publish(kafkaEventObject);
	}

	private User getUserAndValid(QueueToken queueToken, Payment payment) throws CustomException {
		User user = userRepository.findById(queueToken.userId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		if (!user.checkEnoughAmount(payment.amount()))
			throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
		return user;
	}

	private Seat getSeat(Reservation reservation) throws CustomException {
		return seatRepository.findById(reservation.seatId())
			.orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
	}

	private Payment getPayment(Reservation reservation) throws CustomException {
		return paymentRepository.findByReservationId(reservation.id())
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
	}

	private Reservation getReservation(PaymentCommand command) throws CustomException {
		return reservationRepository.findById(command.reservationId())
			.orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
	}

	private QueueToken getQueueTokenAndValid(PaymentCommand command) throws CustomException {
		QueueToken queueToken = queueTokenRepository.findQueueTokenByTokenId(command.queueTokenId());
		QueueTokenUtil.validateQueueToken(queueToken);
		return queueToken;
	}
}
