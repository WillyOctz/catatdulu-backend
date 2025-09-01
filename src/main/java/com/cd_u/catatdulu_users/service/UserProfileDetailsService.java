package com.cd_u.catatdulu_users.service;

import com.cd_u.catatdulu_users.model.UserModel;
import com.cd_u.catatdulu_users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserProfileDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserModel existingProfile = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
        return User.builder()
                .username(existingProfile.getEmail())
                .password(existingProfile.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
