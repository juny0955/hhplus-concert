package kr.hhplus.be.server.adapters.out.persistence.concertRank;

import java.util.List;
import java.util.UUID;

import kr.hhplus.be.server.application.soldoutRank.port.out.RedisSoldOutRankRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisSoldOutRankRepositoryImpl implements RedisSoldOutRankRepository {

	private static final String SOLD_OUT_RANK_KEY = "concert:ranking:soldout";
	private static final String UPDATE_RANK_SCRIPT = """
			-- 랭킹 추가
			redis.call('ZADD', KEYS[1], ARGV[2], ARGV[1])
			
			-- 상위 100위만 유지
			local count = redis.call('ZCARD', KEYS[1])
			if count > tonumber(ARGV[3]) then
				redis.call('ZREMRANGEBYRANK', KEYS[1], ARGV[3], -1)
			end
			
			-- 현재 순위 반환
			local rank = redis.call('ZRANK', KEYS[1], ARGV[1])
			if rank then
				return rank + 1
			else
				return -1
			end
			""";

	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * 콘서트 매진 랭킹 업데이트
	 * @param concertId 매진 콘서트 ID
	 * @param score 점수
	 * @return 랭킹
	 */
	public Long updateRank(UUID concertId, long score) {
		DefaultRedisScript<Long> script = new DefaultRedisScript<>();
		script.setScriptText(UPDATE_RANK_SCRIPT);
		script.setResultType(Long.class);

		List<String> keys = List.of(SOLD_OUT_RANK_KEY);
		Long result = redisTemplate.execute(script, keys, concertId.toString(), score, "100");

		return result > 0 ? result : null;
	}
}
