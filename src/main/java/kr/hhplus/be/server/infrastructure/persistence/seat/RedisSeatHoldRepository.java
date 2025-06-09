package kr.hhplus.be.server.infrastructure.persistence.seat;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.seat.SeatHoldRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisSeatHoldRepository implements SeatHoldRepository {

	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public void hold(UUID seatId, UUID userId) {

	}

	@Override
	public boolean isHoldSeat(UUID seatId, UUID userId) {
		return false;
	}

	@Override
	public void deleteHold(UUID seatId, UUID userId) {

	}
}
