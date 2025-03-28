package com.example.sparta_ticketing.common.redis.aop;

import com.example.sparta_ticketing.common.redis.concurrency.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

    private final RedisLockService redisLockService;

    @Around("@annotation(redisLock)")
    public Object applyRedisLock(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        String lockKey = redisLock.key();

        redisLockService.waitForLock();
        try {
            return joinPoint.proceed();
        } finally {
            redisLockService.unlock(lockKey);
        }
    }
}
