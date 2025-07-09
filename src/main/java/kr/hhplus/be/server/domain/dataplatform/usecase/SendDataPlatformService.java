package kr.hhplus.be.server.domain.dataplatform.usecase;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.dataplatform.port.out.GetConcertPort;
import kr.hhplus.be.server.domain.dataplatform.port.out.SendReservationDataPort;
import kr.hhplus.be.server.domain.payment.domain.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendDataPlatformService {

	private final SendReservationDataPort sendReservationDataPort;
	private final GetConcertPort getConcertPort;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void sendDataPlatform(PaymentSuccessEvent event) {
		try {
			Concert concert = getConcertPort.getConcertByConcertDateId(event.seat().concertDateId());
			sendReservationDataPort.send(event, concert);
		} catch (Exception e) {
			log.warn("데이터 플랫폼 전송 실패");
			// TODO 실패시 재시도 처리
		}
	}
}
