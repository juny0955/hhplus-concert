package kr.hhplus.be.server.domain.concert.adapter.out.persistence.seathold;

import java.time.Duration;
import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.concert.port.out.seathold.CheckHoldSeatPort;
import kr.hhplus.be.server.domain.concert.port.out.seathold.HasHoldSeatPort;
import kr.hhplus.be.server.domain.concert.port.out.seathold.HoldSeatPort;
import kr.hhplus.be.server.domain.concert.port.out.seathold.ReleaseSeatHoldPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisSeatHoldRepository implements HoldSeatPort, CheckHoldSeatPort, HasHoldSeatPort, ReleaseSeatHoldPort {

	private static final Duration HOLD_DURATION = Duration.ofMinutes(5);
	private static final String SEAT_HOLD_PREFIX = "concert:hold:";

	private final RedisTemplate<String, String> seatHoldRedisTemplate;

	@Override
	public void holdSeat(UUID seatId, UUID userId) {
		String key = SEAT_HOLD_PREFIX + seatId;
		String value = userId.toString();

		seatHoldRedisTemplate.opsForValue().set(key, value, HOLD_DURATION);
	}

	@Override
	public boolean checkHoldSeat(UUID seatId) {
		String key = SEAT_HOLD_PREFIX + seatId;
		return seatHoldRedisTemplate.hasKey(key);
	}

	@Override
	public void hasHoldSeat(UUID seatId, UUID userId) throws CustomException {
		String key = SEAT_HOLD_PREFIX + seatId;
		String holdUserId = seatHoldRedisTemplate.opsForValue().get(key);

		if (holdUserId == null || !holdUserId.equals(userId.toString()))
			throw new CustomException(ErrorCode.RESERVATION_EXPIRED);
	}

	@Override
	public void releaseSeatHold(UUID seatId, UUID userId) {
		String key = SEAT_HOLD_PREFIX + seatId;
		seatHoldRedisTemplate.delete(key);
	}
}
