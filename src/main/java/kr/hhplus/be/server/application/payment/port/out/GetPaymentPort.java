package kr.hhplus.be.server.application.payment.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.exception.CustomException;

public interface GetPaymentPort {
	Payment getPaymentByReservationId(UUID reservationId) throws CustomException;
}
