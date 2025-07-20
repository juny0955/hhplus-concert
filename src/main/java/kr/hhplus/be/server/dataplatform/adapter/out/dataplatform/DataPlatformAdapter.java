package kr.hhplus.be.server.dataplatform.adapter.out.dataplatform;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.dataplatform.adapter.out.dataplatform.request.ReservationDataRequest;
import kr.hhplus.be.server.dataplatform.port.out.DataPlatformOutPort;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataPlatformAdapter implements DataPlatformOutPort {

	private final DataPlatformClient dataPlatformClient;

	@Override
	public void send(PaymentSuccessEvent event, Concert concert) {
		dataPlatformClient.sendReservationData(ReservationDataRequest.of(event, concert));
	}
}
