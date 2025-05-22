// backend/gcv-service/src/main/java/com/pistore/gcv/GCVController.java
package com.pistore.gcv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gcv")
public class GCVController {
    @Autowired
    private GCVService gcvService;

    @GetMapping("/rate")
    public GCVDTO getGCVRate() {
        return gcvService.getGCVRate();
    }
}
