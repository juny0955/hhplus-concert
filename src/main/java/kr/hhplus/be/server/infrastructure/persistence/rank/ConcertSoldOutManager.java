package kr.hhplus.be.server.infrastructure.persistence.rank;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.event.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConcertSoldOutManager {

	private static final int MAX_SEAT_COUNT = 50;

	private final ConcertRepository concertRepository;
	private final ConcertSoldOutRankRepository concertSoldOutRankRepository;

	@Transactional
	public void processUpdateRanking(PaymentSuccessEvent event, UUID concertId, int seatSize) throws CustomException {
		Concert concert = getConcert(concertId);

		long score = calcScore(event.payment().updatedAt(), concert.openTime(), seatSize);

		Long rank = concertSoldOutRankRepository.updateRank(concertId, score);
		concertRepository.save(concert.soldOut(event.payment().updatedAt()));

		log.info("콘서트 매진 랭킹 업데이트 - CONCERT_ID: {}, RANKING: {}", concertId, rank);
	}

	private Concert getConcert(UUID concertId) throws CustomException {
		return concertRepository.findById(concertId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));
	}

	/**
	 * 점수 계산
	 * @param lastSeatPaidTime 마지막 좌석 결제 시간
	 * @param openTime 티켓팅 오픈 시간
	 * @param seatSize 좌석 총 개수
	 * @return 점수
	 */
	private long calcScore(LocalDateTime lastSeatPaidTime, LocalDateTime openTime, int seatSize) {
		long soldOutTime = Duration.between(openTime, lastSeatPaidTime).getSeconds();
		int concertDateScore = 100 - (seatSize / MAX_SEAT_COUNT);
		long openTimeStamp = openTime.toEpochSecond(ZoneOffset.UTC);

		String score = String.format("%d%d%d", soldOutTime, concertDateScore, openTimeStamp);

		return Long.parseLong(score);
	}
}
