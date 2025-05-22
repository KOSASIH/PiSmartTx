// backend/rate-service/src/main/java/com/pistore/rate/RateController.java
package com.pistore.rate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rate")
public class RateController {
    @Autowired
    private RateService rateService;

    @GetMapping("/pi")
    public RateDTO getPiRate(@RequestParam String type) {
        return rateService.getPiRate(type);
    }
}
