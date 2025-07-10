package kr.hhplus.be.server.reservation.adapter.out.external;

import kr.hhplus.be.server.reservation.domain.reservation.ReservationPayload;
import kr.hhplus.be.server.reservation.port.out.SendDataPlatformPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataPlatformClient implements SendDataPlatformPort {

    @Override
    public void send(ReservationPayload reservationPayload) {
        log.info("데이터 플랫폼 전송 성공 - 예약정보: {}", reservationPayload.toString());
    }
}
