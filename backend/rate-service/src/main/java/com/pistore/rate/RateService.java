// backend/rate-service/src/main/java/com/pistore/rate/RateService.java
package com.pistore.rate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RateService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final double FIXED_PI_RATE = 314159.0; // 1 Pi = $314,159

    public RateDTO getPiRate() {
        // Check Redis cache
        String cacheKey = "pi:USD";
        String cachedRate = redisTemplate.opsForValue().get(cacheKey);
        if (cachedRate != null) {
            return new RateDTO("USD", Double.parseDouble(cachedRate));
        }

        // Save rate to MongoDB for historical tracking
        RateRecord record = new RateRecord("USD", FIXED_PI_RATE, System.currentTimeMillis());
        mongoTemplate.save(record, "rate_history");

        // Cache the rate in Redis for 24 hours
        redisTemplate.opsForValue().set(cacheKey, String.valueOf(FIXED_PI_RATE), 24, TimeUnit.HOURS);
        return new RateDTO("USD", FIXED_PI_RATE);
    }
}
