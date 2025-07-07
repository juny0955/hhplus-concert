package kr.hhplus.be.server.concert.adapters.in.event;

import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.concert.ports.out.ConcertDateRepository;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.ports.out.SeatRepository;
import kr.hhplus.be.server.concert.domain.seat.SeatStatus;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.infrastructure.persistence.rank.ConcertSoldOutManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConcertSoldOutRankListener {

	private final ConcertDateRepository concertDateRepository;
	private final SeatRepository seatRepository;
	private final ConcertSoldOutManager concertSoldOutManager;

	/**
	 * 결제 성공시 이벤트 수신
	 * 1. 해당 콘서트 매진 체크
	 * 2. 매진시 랭킹 업데이트
	 * @param event 결제 성공 이벤트 정보
	 */
	@Async
	@EventListener
	public void handleEvent(PaymentSuccessEvent event) {
		try {
			ConcertDate concertDate = getConcertDate(event.seat().concertDateId());

			List<Seat> allSeats = seatRepository.findByConcertDateId(concertDate.id());
			boolean isAllSeatsAssigned = allSeats.stream()
				.allMatch(seatItem -> seatItem.status() == SeatStatus.ASSIGNED);

			if (isAllSeatsAssigned)
				concertSoldOutManager.processUpdateRanking(event, concertDate.concertId(), allSeats.size());

		} catch (Exception e) {
			// TODO: 실패한 이벤트 재시도 OR 예외 처리?
		}
	}

	private ConcertDate getConcertDate(UUID concertDateId) throws CustomException {
		return concertDateRepository.findById(concertDateId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_DATE_NOT_FOUND));
	}
}
