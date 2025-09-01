package com.cd_u.catatdulu_users.controller;

import com.cd_u.catatdulu_users.dto.AuthenticationDTO;
import com.cd_u.catatdulu_users.dto.UserResponseDTO;
import com.cd_u.catatdulu_users.model.UserModel;
import com.cd_u.catatdulu_users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> UserRegister(@RequestBody UserResponseDTO userResponseDTO) {
        UserResponseDTO registeredUser = userService.registerUser(userResponseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateUser(@RequestParam String token) {
        boolean isActivated = userService.activateProfile(token);
        if (isActivated) {
            return ResponseEntity.ok("User has been activated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token is not found or already expired");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody AuthenticationDTO authenticationDTO) {
        try {
            if (!userService.isProfileActive(authenticationDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "Message", "Account has not been activated.please activate it first"
                ));
            }
            Map<String, Object> response = userService.authenticateAndGenerateToken(authenticationDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "Message", e.getMessage()
            ));
        }
    }

    @GetMapping("/current")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        UserModel currentUser = userService.getCurrentProfile();
        return ResponseEntity.ok(userService.toResponseDto(currentUser));
    }

    @GetMapping("/test")
    public String test() {
        return "Test Successful";
    }
}
