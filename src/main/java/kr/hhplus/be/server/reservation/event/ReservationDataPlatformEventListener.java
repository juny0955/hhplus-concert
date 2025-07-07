package kr.hhplus.be.server.reservation.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.infrastructure.external.DataPlatformClient;
import kr.hhplus.be.server.reservation.domain.ReservationCreatedEvent;
import kr.hhplus.be.server.reservation.domain.ReservationDataRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 예약 생성 이벤트를 수신하여 데이터 플랫폼으로 전송하는 리스너
 * 트랜잭션과 관심사를 분리하여 비동기로 처리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationDataPlatformEventListener {

    private final DataPlatformClient dataPlatformClient;

    /**
     * 예약 생성 이벤트 수신 및 데이터 플랫폼 전송
     * 
     * @param event 예약 생성 이벤트
     */
    @Async
    @EventListener
    public void handleReservationCreated(ReservationCreatedEvent event) {
        try {
            log.info("예약 생성 이벤트 수신 - reservationId: {}", event.reservationId());

            dataPlatformClient.sendReservationData(ReservationDataRequest.from(event));
            
            log.info("예약 정보 데이터 플랫폼 전송 완료 - reservationId: {}", event.reservationId());
        } catch (Exception e) {
            log.error("예약 정보 데이터 플랫폼 전송 중 오류 발생 - reservationId: {}", event.reservationId(), e);
        }
    }
}