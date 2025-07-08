package kr.hhplus.be.server.application.seat.service;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.seat.port.out.SeatRepository;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatService {

	private final SeatRepository seatRepository;

	public Seat getSeat(UUID seatId) throws CustomException {
		return seatRepository.findById(seatId).orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
	}

	@Transactional
	public Seat paidSeat(UUID seatId) throws CustomException {
		Seat seat = getSeat(seatId);
		return seatRepository.save(seat.payment());
	}

	@Transactional
	public Seat reserveSeat(UUID seatId) throws CustomException {
		Seat seat = getSeat(seatId);
		return seatRepository.save(seat.reserve());
	}

	@Transactional
	public Seat expireSeat(UUID seatId) throws CustomException {
		Seat seat = getSeat(seatId);
		return seatRepository.save(seat.expired());
	}
}
