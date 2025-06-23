package kr.hhplus.be.server.usecase.reservation.service;

import kr.hhplus.be.server.framework.exception.CustomException;

import java.util.List;

public interface ExpiredReservationService {
    List<CreateReservationResult> processExpiredReservation() throws CustomException;
}
