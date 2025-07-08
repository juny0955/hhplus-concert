package kr.hhplus.be.server.adapters.out.persistence.lock;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockManager {

	private static final String LOCK_PREFIX = "lock:";
	private static final long WAIT_TIME = 3L;
	private static final long LEASE_TIME = 10L;

	private final RedissonClient redissonClient;
	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * 반환 값이 있는 락 프로세스
	 * @param key 락 키값
	 * @param transaction 실행 로직
	 * @return 반환 값
	 */
	public <T> T executeWithLockHasReturn(String key, Callable<T> transaction) throws Exception {
		RLock lock = redissonClient.getLock(LOCK_PREFIX + key);

		try {
			if (lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS))
				return transaction.call();

			throw new CustomException(ErrorCode.LOCK_CONFLICT);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.warn("분산락 획득 대기중 인터럽트 발생: Key - {}", key);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		} finally {
			if (lock.isHeldByCurrentThread())
				lock.unlock();
		}
	}

	/**
	 * 반환 값이 없는 락 프로세스
	 * @param key 락 키값
	 * @param action 실행 로직
	 */
	public void executeWithLock(String key, Runnable action) throws CustomException {
		RLock lock = redissonClient.getLock(LOCK_PREFIX + key);

		try {
			if (lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS)) {
				action.run();
				return;
			}

			throw new CustomException(ErrorCode.LOCK_CONFLICT);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.warn("분산락 획득 대기중 인터럽트 발생: Key - {}", key);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		} finally {
			if (lock.isHeldByCurrentThread())
				lock.unlock();
		}
	}

	/**
	 * 반환값이 있는 일반 Redis 락
	 * @param key 락 키값
	 * @param transaction 실행 로직
	 * @return 반환값
	 * @throws Exception 락 획득 실패 시 예외 발생
	 */
	public <T> T executeWithSimpleLockHasReturn(String key, Callable<T> transaction) throws Exception {
		String lockKey = LOCK_PREFIX + key;

		try {
			Boolean result = redisTemplate.opsForValue().setIfAbsent(
				lockKey, 
				"lock",
				Duration.ofSeconds(LEASE_TIME)
			);
			
			if (Boolean.TRUE.equals(result)) {
				try {
					return transaction.call();
				} finally {
					redisTemplate.delete(lockKey);
				}
			}

			throw new CustomException(ErrorCode.LOCK_CONFLICT);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
