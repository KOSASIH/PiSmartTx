// backend/rate-service/src/main/java/com/pistore/rate/RateService.java
package com.pistore.rate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RateService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ExchangeRateClient exchangeRateClient;

    private static final double INTERNAL_PI_RATE = 314159.0; // 1 Pi = $314,159

    public RateDTO getPiRate(String type) {
        String cacheKey = "pi:" + type + ":USD";
        String cachedRate = redisTemplate.opsForValue().get(cacheKey);
        if (cachedRate != null) {
            return new RateDTO("USD", Double.parseDouble(cachedRate));
        }

        double rate;
        if (type.equals("internal")) {
            rate = INTERNAL_PI_RATE;
        } else {
            rate = exchangeRateClient.getAveragePiRate();
        }

        redisTemplate.opsForValue().set(cacheKey, String.valueOf(rate), 1, TimeUnit.HOURS);
        return new RateDTO("USD", rate);
    }
}
