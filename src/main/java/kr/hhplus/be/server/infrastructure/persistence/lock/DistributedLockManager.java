package kr.hhplus.be.server.infrastructure.persistence.lock;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockManager {

    private final RedissonClient redissonClient;

    private static final String LOCK_PREFIX = "RLock:";
    private static final long WAIT_TIME = 3L;
    private static final long LEASE_TIME = 10L;

	public <T> T executeWithLock(String key, Callable<T> transaction) throws Exception {
		RLock lock = redissonClient.getLock(LOCK_PREFIX + key);
		
		try {
			if (lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS))
				return transaction.call();

			throw new RuntimeException();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("분산락 대기 중 인터럽트 발생", e);
		} finally {
			if (lock.isHeldByCurrentThread())
				lock.unlock();
		}
	}
}
