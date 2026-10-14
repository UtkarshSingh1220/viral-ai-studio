package com.viralstudio.content;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ContentItemRepository extends JpaRepository<ContentItem, UUID> {
    List<ContentItem> findAllByOrderByUpdatedAtDesc();
    List<ContentItem> findByStatusOrderByUpdatedAtDesc(ContentStatus status);
}
