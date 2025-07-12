package kr.hhplus.be.server.payment.adapter.out.external.dataplatform;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.payment.adapter.out.external.dataplatform.request.ReservationDataRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataPlatformClient {

	public void sendReservationData(ReservationDataRequest reservationDataRequest) {
		log.info("데이터 플랫폼 예약 정보 전송 - 예약 정보: {}", reservationDataRequest.toString());
	}
}
