// backend/recommendation-service/src/main/java/com/pistore/recommendation/RecommendationService.java
package com.pistore.recommendation;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RecommendationService {
    // Simulasi model ML (bisa diganti dengan TensorFlow Java atau panggilan ke Python API)
    public List<MerchantDTO> getRecommendations(String userId, double latitude, double longitude) {
        // Logika: Ambil data riwayat transaksi dari MongoDB
        // Panggil model ML untuk menghasilkan rekomendasi
        // Filter berdasarkan jarak menggunakan latitude dan longitude
        List<MerchantDTO> recommendations = new ArrayList<>();
        // Contoh dummy data
        recommendations.add(new MerchantDTO("merchant1", "Toko A", 0.5));
        return recommendations;
    }
}
