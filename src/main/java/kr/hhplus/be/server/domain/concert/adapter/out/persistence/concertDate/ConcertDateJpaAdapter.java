package kr.hhplus.be.server.domain.concert.adapter.out.persistence.concertDate;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.concert.domain.concertDate.ConcertDates;
import kr.hhplus.be.server.domain.concert.port.out.concertDate.GetConcertDatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConcertDateJpaAdapter implements GetConcertDatePort {

    private final JpaConcertDateRepository jpaConcertDateRepository;

    @Override
    public ConcertDate getConcertDate(UUID concertDateId) throws CustomException {
        return jpaConcertDateRepository.findById(concertDateId.toString())
                .map(ConcertDateEntity::toDomain)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_DATE_NOT_FOUND));
    }

    @Override
    public void existsConcertDate(UUID concertDateId) throws CustomException {
        if (!jpaConcertDateRepository.existsById(concertDateId.toString()))
            throw new CustomException(ErrorCode.CONCERT_DATE_NOT_FOUND);
    }

    @Override
    public ConcertDates getAvailableDatesWithSeatCount(UUID concertId) {
        List<Object[]> results = jpaConcertDateRepository.findAvailableDatesWithAvailableSeatCount(concertId.toString());

        return new ConcertDates(results.stream()
                .map(result -> {
                    String id = (String) result[0];
                    String concertIdStr = (String) result[1];
                    LocalDateTime date = (LocalDateTime) result[2];
                    LocalDateTime deadline = (LocalDateTime) result[3];
                    LocalDateTime createdAt = (LocalDateTime) result[4];
                    LocalDateTime updatedAt = (LocalDateTime) result[5];
                    Long seatCount = (Long) result[6];

                    return ConcertDate.builder()
                            .id(UUID.fromString(id))
                            .concertId(UUID.fromString(concertIdStr))
                            .date(date)
                            .deadline(deadline)
                            .remainingSeatCount(seatCount.intValue())
                            .createdAt(createdAt)
                            .updatedAt(updatedAt)
                            .build();
                })
                .toList());
    }
}