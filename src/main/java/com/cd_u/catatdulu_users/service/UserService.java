package com.cd_u.catatdulu_users.service;

import com.cd_u.catatdulu_users.dto.AuthenticationDTO;
import com.cd_u.catatdulu_users.dto.UserResponseDTO;
import com.cd_u.catatdulu_users.model.UserModel;
import com.cd_u.catatdulu_users.repository.UserRepository;
import com.cd_u.catatdulu_users.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailForwadService emailForwadService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${app.send.url}")
    private String sendUrl;

    public UserModel toModel(UserResponseDTO userResponseDTO) {
        return UserModel.builder()
                .id(userResponseDTO.getId())
                .email(userResponseDTO.getEmail())
                .password(passwordEncoder.encode(userResponseDTO.getPassword()))
                .firstName(userResponseDTO.getFirstName())
                .lastName(userResponseDTO.getLastName())
                .profileImage(userResponseDTO.getProfileImage())
                .createdAt(userResponseDTO.getCreatedAt())
                .updatedAt(userResponseDTO.getUpdatedAt())
                .build();
    }

    public UserResponseDTO registerUser(UserResponseDTO userResponseDTO) {
        UserModel newUser = toModel(userResponseDTO);
        newUser.setActivationToken(UUID.randomUUID().toString());
        newUser = userRepository.save(newUser);
        // send activation link through email
        String activationLink = sendUrl + "/api/users/activate?token=" + newUser.getActivationToken();
        String subject = "Please Activate Your Catat Dulu Account";
        String body = "Click on the following link to activate your account: " + activationLink;
        emailForwadService.sendMail(newUser.getEmail(), subject, body);
        return toResponseDto(newUser);
    }

    public UserResponseDTO toResponseDto(UserModel userModel) {
        return UserResponseDTO.builder()
                .id(userModel.getId())
                .email(userModel.getEmail())
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .profileImage(userModel.getProfileImage())
                .createdAt(userModel.getCreatedAt())
                .updatedAt(userModel.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken) {
        return userRepository.findByActivationToken(activationToken)
                .map(userModel -> {
                    userModel.setIsOnline(true);
                    userRepository.save(userModel);
                    return true;
                })
                .orElse(false);
    }

    public boolean isProfileActive(String email) {
        return userRepository.findByEmail(email)
                .map(UserModel::getIsOnline)
                .orElse(false);
    }

    public UserModel getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email." + authentication.getName()));
    }

    public UserResponseDTO getPublicProfile(String email) {
        UserModel currentUser = null;
        if (email == null) {
            currentUser = getCurrentProfile();
        } else {
            currentUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email" + email));
        }

        return UserResponseDTO.builder()
                .id(currentUser.getId())
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .email(currentUser.getEmail())
                .profileImage(currentUser.getProfileImage())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthenticationDTO authenticationDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationDTO.getEmail(), authenticationDTO.getPassword()));
            // Generate a JWT token
            String token = jwtUtil.generateToken(authenticationDTO.getEmail());
            return Map.of(
                    "token", token,
                    "user", getPublicProfile(authenticationDTO.getEmail())
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}
