package kr.hhplus.be.server.infrastructure.persistence.reservation;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.concertDate.ConcertDateRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.queue.QueueTokenRepository;
import kr.hhplus.be.server.domain.queue.QueueTokenUtil;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationDomainResult;
import kr.hhplus.be.server.domain.reservation.ReservationDomainService;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatHoldRepository;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.usecase.reservation.input.ReserveSeatCommand;
import kr.hhplus.be.server.usecase.reservation.interactor.ReservationTransactionResult;
import kr.hhplus.be.server.usecase.reservation.interactor.ReservationTransactionService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationTransactionManager implements ReservationTransactionService {

	private final ReservationRepository reservationRepository;
	private final QueueTokenRepository queueTokenRepository;
	private final ConcertRepository concertRepository;
	private final ConcertDateRepository concertDateRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final SeatRepository seatRepository;
	private final PaymentRepository paymentRepository;
	private final ReservationDomainService reservationDomainService;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public ReservationTransactionResult processReservationTransaction(ReserveSeatCommand command) throws CustomException {
		QueueToken queueToken = getQueueTokenAndValid(command);
		checkExistsConcert(command.concertId());

		ConcertDate concertDate = getConcertDate(command.concertDateId());
		Seat seat = getSeat(command.seatId(), command.concertDateId());

		ReservationDomainResult result = reservationDomainService.processReservation(concertDate, seat, queueToken.userId());

		updateSeatStatusReserved(result.seat());
		Reservation savedReservation = reservationRepository.save(result.reservation());
		Payment savedPayment 	 = paymentRepository.save(Payment.of(queueToken.userId(), savedReservation.id(), result.seat().price()));

		seatHoldRepository.hold(result.seat().id(), queueToken.userId());
		return new ReservationTransactionResult(savedReservation, savedPayment, seat, queueToken.userId());
	}

	private void updateSeatStatusReserved(Seat seat) throws CustomException {
		int updateSeat = seatRepository.updateStatusReserved(seat.id());
		if (updateSeat <= 0)
			throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);
	}

	private Seat getSeat(UUID seatId, UUID concertDateId) throws CustomException {
		return seatRepository.findBySeatIdAndConcertDateId(seatId, concertDateId)
			.orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
	}


	private ConcertDate getConcertDate(UUID concertDateId) throws CustomException {
		return concertDateRepository.findById(concertDateId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_DATE_NOT_FOUND));
	}

	private void checkExistsConcert(UUID concertId) throws CustomException {
		if (!concertRepository.existsById(concertId))
			throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);
	}

	private QueueToken getQueueTokenAndValid(ReserveSeatCommand command) throws CustomException {
		QueueToken queueToken = queueTokenRepository.findQueueTokenByTokenId(command.queueTokenId());
		QueueTokenUtil.validateActiveQueueToken(queueToken);
		return queueToken;
	}
}
