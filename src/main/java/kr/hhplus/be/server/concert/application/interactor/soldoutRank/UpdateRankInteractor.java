package kr.hhplus.be.server.concert.application.interactor.soldoutRank;

import kr.hhplus.be.server.common.framework.exception.CustomException;
import kr.hhplus.be.server.common.framework.exception.ErrorCode;
import kr.hhplus.be.server.concert.application.service.SoldOutApplicationService;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.SeatStatus;
import kr.hhplus.be.server.concert.ports.out.ConcertDateRepository;
import kr.hhplus.be.server.concert.ports.out.SeatRepository;
import kr.hhplus.be.server.payment.domain.UpdateRankEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateRankInteractor {

    private final SoldOutApplicationService soldOutApplicationService;
    private final ConcertDateRepository concertDateRepository;
    private final SeatRepository seatRepository;

    @Async
    @EventListener
    public void handleEvent(UpdateRankEvent event) {
        try {
            ConcertDate concertDate = getConcertDate(event.seat().concertDateId());

            List<Seat> allSeats = seatRepository.findByConcertDateId(concertDate.id());
            boolean isAllSeatsAssigned = allSeats.stream()
                    .allMatch(seatItem -> seatItem.status() == SeatStatus.ASSIGNED);

            if (isAllSeatsAssigned)
                soldOutApplicationService.processUpdateRanking(event, concertDate.concertId(), allSeats.size());

        } catch (Exception e) {
            // TODO: 실패한 이벤트 재시도 OR 예외 처리?
        }
    }

    private ConcertDate getConcertDate(UUID concertDateId) throws CustomException {
        return concertDateRepository.findById(concertDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_DATE_NOT_FOUND));
    }

}
