package kr.hhplus.be.server.usecase.reservation.interactor;

import kr.hhplus.be.server.usecase.concert.SeatRepository;
import kr.hhplus.be.server.usecase.payment.PaymentRepository;
import kr.hhplus.be.server.usecase.queue.QueueTokenRepository;
import kr.hhplus.be.server.usecase.reservation.ReservationRepository;
import kr.hhplus.be.server.usecase.reservation.SeatLockRepository;
import kr.hhplus.be.server.usecase.reservation.SeatHoldRepository;
import kr.hhplus.be.server.usecase.reservation.input.ReservationInput;
import kr.hhplus.be.server.usecase.reservation.input.ReserveSeatCommand;
import kr.hhplus.be.server.usecase.reservation.output.ReservationOutput;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReservationInteractor implements ReservationInput {

	private final ReservationRepository reservationRepository;
	private final QueueTokenRepository queueTokenRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final SeatLockRepository seatLockRepository;
	private final SeatRepository seatRepository;
	private final PaymentRepository paymentRepository;
	private final ReservationOutput reservationOutput;

	@Override
	public void reserveSeat(ReserveSeatCommand command) {

	}
}
