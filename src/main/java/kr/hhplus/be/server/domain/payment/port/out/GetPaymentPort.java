package kr.hhplus.be.server.domain.payment.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.common.exception.CustomException;

public interface GetPaymentPort {
	Payment getPaymentByReservationId(UUID reservationId) throws CustomException;
}
