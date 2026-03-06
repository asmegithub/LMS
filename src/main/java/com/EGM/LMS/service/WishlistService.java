package com.EGM.LMS.service;

import com.EGM.LMS.dto.WishlistDTO;

import java.util.List;
import java.util.UUID;

public interface WishlistService {
    WishlistDTO createWishlist(WishlistDTO wishlist);
    List<WishlistDTO> getMyWishlists();
    WishlistDTO addToWishlist(UUID courseId);
    void removeFromWishlist(UUID courseId);
    boolean isInWishlist(UUID courseId);
    List<WishlistDTO> getAllWishlists();
    WishlistDTO getWishlist(UUID wishlistId);
    WishlistDTO updateWishlist(UUID wishlistId, WishlistDTO wishlist);
    void deleteWishlist(UUID wishlistId);
}
