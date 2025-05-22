// backend/ar-service/src/main/java/com/pistore/ar/ARMerchantController.java
package com.pistore.ar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/ar/merchants")
public class ARMerchantController {
    @Autowired
    private ARMerchantService arMerchantService;

    @GetMapping
    public List<MerchantARDTO> getNearbyMerchants(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius) {
        return arMerchantService.findNearbyMerchants(latitude, longitude, radius);
    }
}
