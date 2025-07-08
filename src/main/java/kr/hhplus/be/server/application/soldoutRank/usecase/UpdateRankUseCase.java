package kr.hhplus.be.server.application.soldoutRank.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.server.application.concertDate.port.out.ConcertDateRepository;
import kr.hhplus.be.server.application.seat.port.out.SeatRepository;
import kr.hhplus.be.server.application.soldoutRank.service.SoldOutService;
import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UpdateRankUseCase {

    private final SoldOutService soldOutService;
    private final ConcertDateRepository concertDateRepository;
    private final SeatRepository seatRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateRank(PaymentSuccessEvent event) {
        try {
            ConcertDate concertDate = getConcertDate(event.seat().concertDateId());

            List<Seat> allSeats = seatRepository.findByConcertDateId(concertDate.id());
            boolean isAllSeatsAssigned = allSeats.stream()
                    .allMatch(seatItem -> seatItem.status() == SeatStatus.ASSIGNED);

            if (isAllSeatsAssigned)
                soldOutService.processUpdateRanking(event, concertDate.concertId(), allSeats.size());

        } catch (Exception e) {
            // TODO: 실패한 이벤트 재시도 OR 예외 처리?
        }
    }

    private ConcertDate getConcertDate(UUID concertDateId) throws CustomException {
        return concertDateRepository.findById(concertDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_DATE_NOT_FOUND));
    }
}
