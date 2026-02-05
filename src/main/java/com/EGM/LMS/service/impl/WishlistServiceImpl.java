package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.dto.WishlistDTO;
import com.EGM.LMS.model.Wishlist;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.repository.WishlistRepository;
import com.EGM.LMS.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    public WishlistDTO createWishlist(WishlistDTO wishlist) {
        return toDto(wishlistRepository.save(toEntity(wishlist)));
    }

    @Override
    public List<WishlistDTO> getAllWishlists() {
        var wishlists = wishlistRepository.findAll();
        var wishlistDtos = new java.util.ArrayList<WishlistDTO>();
        for (Wishlist wishlist : wishlists) {
            wishlistDtos.add(toDto(wishlist));
        }
        return wishlistDtos;
    }

    @Override
    public WishlistDTO getWishlist(UUID wishlistId) {
        return toDto(wishlistRepository.findById(wishlistId).orElseThrow());
    }

    @Override
    public WishlistDTO updateWishlist(UUID wishlistId, WishlistDTO wishlist) {
        wishlistRepository.findById(wishlistId).orElseThrow();
        var entity = toEntity(wishlist);
        entity.setId(wishlistId);
        return toDto(wishlistRepository.save(entity));
    }

    @Override
    public void deleteWishlist(UUID wishlistId) {
        wishlistRepository.deleteById(wishlistId);
    }

    private Wishlist toEntity(WishlistDTO wishlist) {
        var userId = wishlist.getUser() != null ? wishlist.getUser().getId() : null;
        var courseId = wishlist.getCourse() != null ? wishlist.getCourse().getId() : null;
        return Wishlist.builder()
                .user(userId != null ? userRepository.findById(userId).orElse(null) : null)
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .build();
    }

    private WishlistDTO toDto(Wishlist wishlist) {
        return WishlistDTO.builder()
                .id(wishlist.getId())
                .user(wishlist.getUser() != null ? UserDTO.builder().id(wishlist.getUser().getId()).build() : null)
                .course(wishlist.getCourse() != null ? CourseDTO.builder().id(wishlist.getCourse().getId()).build() : null)
                .createdAt(wishlist.getCreatedAt())
                .updatedAt(wishlist.getUpdatedAt())
                .build();
    }
}
