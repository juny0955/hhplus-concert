package kr.hhplus.be.server.payment.port.out;

import java.util.UUID;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.common.exception.CustomException;

public interface GetPaymentPort {
	Payment getPaymentByReservationId(UUID reservationId) throws CustomException;
}
