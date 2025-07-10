package kr.hhplus.be.server.domain.reservation.usecase;

import kr.hhplus.be.server.domain.reservation.domain.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.reservation.domain.ReservationPayload;
import kr.hhplus.be.server.domain.reservation.port.out.SendDataPlatformPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendDataPlatformService {

    private final SendDataPlatformPort sendDataPlatformPort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void sendDataPlatform(ReservationCreatedEvent event) {
        try {
            sendDataPlatformPort.send(ReservationPayload.from(event));
        } catch (Exception e) {
            log.warn("데이터 플랫폼 전송 실패");
        }
    }
}
