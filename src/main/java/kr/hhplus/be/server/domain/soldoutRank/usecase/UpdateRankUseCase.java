package kr.hhplus.be.server.domain.soldoutRank.usecase;

import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.payment.domain.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.seat.domain.SeatStatus;
import kr.hhplus.be.server.domain.soldoutRank.domain.SoldOutRank;
import kr.hhplus.be.server.domain.soldoutRank.port.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateRankUseCase {

    private static final int MAX_SEAT_COUNT = 50;

    private final GetSeatsPort getSeatsPort;
    private final SaveSoldOutRankPort saveSoldOutRankPort;
    private final GetConcertPort getConcertPort;
    private final SoldOutConcertPort soldOutConcertPort;
    private final RedisSoldOutRankRepository redisSoldOutRankRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateRank(PaymentSuccessEvent event) {
        try {
            List<Seat> allSeats = getSeatsPort.getSeats(event.seat().concertDateId());
            boolean isAllSeatsAssigned = allSeats.stream()
                    .allMatch(seatItem -> seatItem.status() == SeatStatus.ASSIGNED);

            if (!isAllSeatsAssigned)
                return;

            Concert concert = getConcertPort.getConcertByConcertDateId(event.seat().concertDateId());

            long soldOutTime = Duration.between(concert.openTime(), event.payment().updatedAt()).getSeconds();
            long score = calcScore(soldOutTime, concert.openTime(), allSeats.size());

            Long rank = redisSoldOutRankRepository.updateRank(concert.id(), score);
            saveSoldOutRankPort.save(SoldOutRank.of(concert.id(), score, soldOutTime));
            soldOutConcertPort.soldOutConcert(concert.id(), event.payment().updatedAt());
            log.info("콘서트 매진 랭킹 업데이트 - CONCERT_ID: {}, RANKING: {}", concert.id(), rank);
        } catch (Exception e) {
            // TODO: 실패한 이벤트 재시도 OR 예외 처리?
        }
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
