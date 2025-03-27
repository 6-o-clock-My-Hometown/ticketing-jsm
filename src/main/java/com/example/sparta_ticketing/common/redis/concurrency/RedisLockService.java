package com.example.sparta_ticketing.common.redis.concurrency;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String LOCK_KEY = "redisLock";
    private static final long LOCK_EXPIRATION_TIME = 5000; // 5초 (데드락 방지)
    private static final long RETRY_DELAY = 100; // 0.1초마다 재시도

    public boolean acquireLock() {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY,
                "true",
                LOCK_EXPIRATION_TIME,
                TimeUnit.MILLISECONDS);
        return Boolean.TRUE.equals(success);

    }

    public void waitForLock() throws InterruptedException {
        while (!acquireLock()) {
            Thread.sleep(RETRY_DELAY);
        }
    }

    public void unlock(String lockKey) {
        redisTemplate.delete(lockKey);
    }
}
