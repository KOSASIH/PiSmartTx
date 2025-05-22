// backend/ar-service/src/main/java/com/pistore/ar/ARMerchantService.java
package com.pistore.ar;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ARMerchantService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<MerchantARDTO> findNearbyMerchants(double latitude, double longitude, double radius) {
        // Query MongoDB untuk merchant dalam radius tertentu
        // Gunakan geospatial query untuk efisiensi
        // Contoh: return mongoTemplate.find(...);
        List<MerchantARDTO> merchants = new ArrayList<>();
        merchants.add(new MerchantARDTO("merchant1", "Toko A", latitude + 0.001, longitude + 0.001, "Promo: Diskon 10%"));
        return merchants;
    }
}
