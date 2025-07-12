package kr.hhplus.be.server.payment.usecase;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.payment.port.out.ConcertQueryPort;
import kr.hhplus.be.server.payment.port.out.DataPlatformOutPort;
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

	private final DataPlatformOutPort dataPlatformOutPort;
	private final ConcertQueryPort concertQueryPort;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void sendDataPlatform(PaymentSuccessEvent event) {
		try {
			Concert concert = concertQueryPort.getConcertByConcertDateId(event.seat().concertDateId());
			dataPlatformOutPort.send(event, concert);
		} catch (Exception e) {
			log.warn("데이터 플랫폼 전송 실패");
			// TODO 실패시 재시도 처리
		}
	}
}
