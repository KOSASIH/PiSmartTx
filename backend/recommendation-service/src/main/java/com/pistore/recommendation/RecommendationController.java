// backend/recommendation-service/src/main/java/com/pistore/recommendation/RecommendationController.java
package com.pistore.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/merchants")
    public List<MerchantDTO> getRecommendedMerchants(@RequestParam String userId, @RequestParam double latitude, @RequestParam double longitude) {
        return recommendationService.getRecommendations(userId, latitude, longitude);
    }
}
