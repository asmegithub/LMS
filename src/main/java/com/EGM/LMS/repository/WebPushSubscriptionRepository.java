package com.EGM.LMS.repository;

import com.EGM.LMS.model.WebPushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WebPushSubscriptionRepository extends JpaRepository<WebPushSubscription, UUID> {
    List<WebPushSubscription> findAllByEndpoint(String endpoint);
    List<WebPushSubscription> findByUser_Id(UUID userId);
    List<WebPushSubscription> findByUser_IdIn(List<UUID> userIds);
    void deleteByEndpointAndUser_Id(String endpoint, UUID userId);
}