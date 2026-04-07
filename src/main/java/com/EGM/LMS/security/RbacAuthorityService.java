package com.EGM.LMS.security;

import com.EGM.LMS.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface RbacAuthorityService {
    Collection<? extends GrantedAuthority> buildAuthorities(User user);
}