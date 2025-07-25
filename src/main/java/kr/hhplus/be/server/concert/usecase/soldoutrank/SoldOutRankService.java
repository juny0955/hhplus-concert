package kr.hhplus.be.server.concert.usecase.soldoutrank;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.SeatStatus;
import kr.hhplus.be.server.concert.domain.soldoutrank.SoldOutRank;
import kr.hhplus.be.server.concert.port.in.soldoutrank.UpdateSoldOutRankUseCase;
import kr.hhplus.be.server.concert.port.out.concert.GetConcertPort;
import kr.hhplus.be.server.concert.port.out.concert.SaveConcertPort;
import kr.hhplus.be.server.concert.port.out.seat.GetSeatPort;
import kr.hhplus.be.server.concert.port.out.soldoutrank.RedisSoldOutRankPort;
import kr.hhplus.be.server.concert.port.out.soldoutrank.SaveSoldOutRankPort;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SoldOutRankService implements UpdateSoldOutRankUseCase {

    private static final int MAX_SEAT_COUNT = 50;

    private final GetSeatPort getSeatPort;
    private final SaveSoldOutRankPort saveSoldOutRankPort;
    private final GetConcertPort getConcertPort;
    private final SaveConcertPort saveConcertPort;
    private final RedisSoldOutRankPort redisSoldOutRankPort;

    public void updateSoldOutRank(PaymentSuccessEvent event) {
        try {
            List<Seat> allSeats = getSeatPort.getSeatsByConcertDateId(event.seatId());
            boolean isAllSeatsAssigned = allSeats.stream()
                    .allMatch(seatItem -> seatItem.status() == SeatStatus.ASSIGNED);

            if (!isAllSeatsAssigned)
                return;

            Concert concert = getConcertPort.getConcertByConcertDateId(event.seatId());

            long soldOutTime = Duration.between(concert.openTime(),null).getSeconds();
            // long soldOutTime = Duration.between(concert.openTime(), event.paymentId().updatedAt()).getSeconds();
            long score = calcScore(soldOutTime, concert.openTime(), allSeats.size());

            Long rank = redisSoldOutRankPort.updateRank(concert.id(), score);
            saveSoldOutRankPort.save(SoldOutRank.of(concert.id(), score, soldOutTime));
            saveConcertPort.saveConcert(concert.soldOut(null));
            // saveConcertPort.saveConcert(concert.soldOut(event.payment().updatedAt()));
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
