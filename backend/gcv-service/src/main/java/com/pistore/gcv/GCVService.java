// backend/gcv-service/src/main/java/com/pistore/gcv/GCVService.java
package com.pistore.gcv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class GCVService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final double FIXED_GCV_RATE = 314159.0; // 1 Pi = $314,159

    public GCVDTO getGCVRate() {
        // Check Redis cache
        String cacheKey = "gcv:USD";
        String cachedRate = redisTemplate.opsForValue().get(cacheKey);
        if (cachedRate != null) {
            return new GCVDTO("USD", Double.parseDouble(cachedRate));
        }

        // Return fixed GCV rate and cache it
        redisTemplate.opsForValue().set(cacheKey, String.valueOf(FIXED_GCV_RATE), 24, TimeUnit.HOURS);
        return new GCVDTO("USD", FIXED_GCV_RATE);
    }
}
