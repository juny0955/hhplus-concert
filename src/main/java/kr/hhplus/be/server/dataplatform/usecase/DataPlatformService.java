package kr.hhplus.be.server.dataplatform.usecase;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.dataplatform.port.in.PaymentSuccessUseCase;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;
import kr.hhplus.be.server.payment.port.out.ConcertQueryPort;
import kr.hhplus.be.server.dataplatform.port.out.DataPlatformOutPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataPlatformService implements PaymentSuccessUseCase {

	private final DataPlatformOutPort dataPlatformOutPort;
	private final ConcertQueryPort concertQueryPort;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void sendDataPlatform(PaymentSuccessEvent event) {
		try {
			Concert concert = concertQueryPort.getConcertByConcertDateId(event.seatId());
			dataPlatformOutPort.send(event, concert);
		} catch (Exception e) {
			log.warn("데이터 플랫폼 전송 실패");
			// TODO 실패시 재시도 처리
		}
	}
}
