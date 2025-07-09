package kr.hhplus.be.server.domain.reservation.usecase;

import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.reservation.port.in.PaidReservationUseCase;
import kr.hhplus.be.server.domain.reservation.port.out.GetReservationPort;
import kr.hhplus.be.server.domain.reservation.port.out.SaveReservationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaidReservationService implements PaidReservationUseCase {

	private final GetReservationPort getReservationPort;
	private final SaveReservationPort saveReservationPort;

	@Override
	@DistributedLock(key = "reservation:#reservationId")
	@Transactional
	public Reservation paidReservation(UUID reservationId) throws CustomException {
		Reservation reservation = getReservationPort.getReservation(reservationId);
		return saveReservationPort.saveReservation(reservation.paid());
	}
}
