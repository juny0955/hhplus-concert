package kr.hhplus.be.server.application.soldoutRank.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.concert.port.out.ConcertRepository;
import kr.hhplus.be.server.application.soldoutRank.port.out.RedisSoldOutRankRepository;
import kr.hhplus.be.server.application.soldoutRank.port.out.SoldOutRankRepository;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.soldoutRank.SoldOutRank;
import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SoldOutService {

	private static final int MAX_SEAT_COUNT = 50;

	private final ConcertRepository concertRepository;
	private final RedisSoldOutRankRepository redisSoldOutRankRepository;
	private final SoldOutRankRepository soldOutRankRepository;

	@Transactional
	public void processUpdateRanking(PaymentSuccessEvent event, UUID concertId, int seatSize) throws CustomException {
		Concert concert = getConcert(concertId);

		long soldOutTime = Duration.between(concert.openTime(), event.payment().updatedAt()).getSeconds();
		long score = calcScore(soldOutTime, concert.openTime(), seatSize);

		Long rank = redisSoldOutRankRepository.updateRank(concertId, score);
		concertRepository.save(concert.soldOut(event.payment().updatedAt()));
		soldOutRankRepository.save(SoldOutRank.of(concertId, score, soldOutTime));

		log.info("콘서트 매진 랭킹 업데이트 - CONCERT_ID: {}, RANKING: {}", concertId, rank);
	}

	private Concert getConcert(UUID concertId) throws CustomException {
		return concertRepository.findById(concertId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));
	}

	/**
	 * 점수 계산
	 * @param soldOutTime 매진 소요 시간
	 * @param openTime 티켓팅 오픈 시간
	 * @param seatSize 좌석 총 개수
	 * @return 점수
	 */
	private long calcScore(long soldOutTime, LocalDateTime openTime, int seatSize) {
		int concertDateScore = 100 - (seatSize / MAX_SEAT_COUNT);
		long openTimeStamp = openTime.toEpochSecond(ZoneOffset.UTC);

		String score = String.format("%d%d%d", soldOutTime, concertDateScore, openTimeStamp);

		return Long.parseLong(score);
	}
}
