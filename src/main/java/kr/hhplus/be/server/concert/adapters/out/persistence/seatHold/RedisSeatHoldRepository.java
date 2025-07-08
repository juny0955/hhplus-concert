package kr.hhplus.be.server.concert.adapters.out.persistence.seatHold;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.ports.out.SeatHoldRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisSeatHoldRepository implements SeatHoldRepository {

	private static final Duration HOLD_DURATION = Duration.ofMinutes(5);
	private static final String SEAT_HOLD_PREFIX = "seat:hold:";

	private final RedisTemplate<String, String> seatHoldRedisTemplate;

	@Override
	public void hold(UUID seatId, UUID userId) {
		String key = SEAT_HOLD_PREFIX + seatId;
		String value = userId.toString();

		seatHoldRedisTemplate.opsForValue().set(key, value, HOLD_DURATION);
	}

	@Override
	public boolean hasHoldByUser(UUID seatId, UUID userId) {
		String key = SEAT_HOLD_PREFIX + seatId;
		String holdUserId = seatHoldRedisTemplate.opsForValue().get(key);

		if (holdUserId == null)
			return false;

		return holdUserId.equals(userId.toString());
	}

	public boolean isHoldSeat(UUID seatId) {
		String key = SEAT_HOLD_PREFIX + seatId;
		return seatHoldRedisTemplate.hasKey(key);
	}

	@Override
	public void deleteHold(UUID seatId, UUID userId) {
		String key = SEAT_HOLD_PREFIX + seatId;
		seatHoldRedisTemplate.delete(key);
	}
}
