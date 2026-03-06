package com.EGM.LMS.repository;

import com.EGM.LMS.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {

    List<Wishlist> findByUser_IdOrderByCreatedAtDesc(UUID userId);

    Optional<Wishlist> findByUser_IdAndCourse_Id(UUID userId, UUID courseId);
}
