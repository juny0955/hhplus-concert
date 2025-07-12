package kr.hhplus.be.server.common.aop;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {

	private final RedissonClient redissonClient;
	private final SpelExpressionParser parser = new SpelExpressionParser();

	@Around("@annotation(distributedLock)")
	public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
		String lockKey = distributedLock.prefix() + parseLockKey(distributedLock.key(), joinPoint);
		RLock lock = redissonClient.getLock(lockKey);

		try {
			boolean isLocked = lock.tryLock(
				distributedLock.waitTime(),
				distributedLock.leaseTime(),
				TimeUnit.SECONDS
			);

			if (!isLocked) {
				log.warn("분산락 획득 실패: Key - {}", lockKey);
				throw new CustomException(ErrorCode.LOCK_CONFLICT);
			}

			return joinPoint.proceed();

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.warn("분산락 획득 대기중 인터럽트 발생: Key - {}", lockKey);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	private String parseLockKey(String key, ProceedingJoinPoint joinPoint) {
		if (!key.contains("#"))
			return key;

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		String[] parameterNames = signature.getParameterNames();
		Object[] args = joinPoint.getArgs();

		EvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < parameterNames.length; i++)
			context.setVariable(parameterNames[i], args[i]);

		Expression expression = parser.parseExpression(key);
		return expression.getValue(context, String.class);
	}
}
