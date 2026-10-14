package com.viralstudio.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PerformanceMetricRepository extends JpaRepository<PerformanceMetric, UUID> {
    List<PerformanceMetric> findAllByOrderByCapturedAtDesc();
    List<PerformanceMetric> findByContentIdOrderByCapturedAtDesc(UUID contentId);
}
