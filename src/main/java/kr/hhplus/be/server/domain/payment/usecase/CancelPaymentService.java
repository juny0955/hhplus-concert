package kr.hhplus.be.server.domain.payment.usecase;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.payment.port.in.CancelPaymentUseCase;
import kr.hhplus.be.server.domain.payment.port.out.GetPaymentPort;
import kr.hhplus.be.server.domain.payment.port.out.SavePaymentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelPaymentService implements CancelPaymentUseCase {

    private final GetPaymentPort getPaymentPort;
    private final SavePaymentPort savePaymentPort;

    @Override
    public Payment cancelPayment(UUID reservationId) throws CustomException {
        Payment payment = getPaymentPort.getPaymentByReservationId(reservationId);
        return savePaymentPort.save(payment.cancel());
    }
}
