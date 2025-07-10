package kr.hhplus.be.server.dataplatform.usecase;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.dataplatform.port.out.GetConcertPort;
import kr.hhplus.be.server.dataplatform.port.out.SendReservationDataPort;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataPlatformService {

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
