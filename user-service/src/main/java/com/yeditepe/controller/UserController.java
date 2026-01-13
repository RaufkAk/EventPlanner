package com.yeditepe.controller;

import com.yeditepe.dto.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final com.yeditepe.service.UserService userService;

    @GetMapping("/{id}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable("id") Long id) {
        boolean exists = userService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(new UserProfile(
                        user.getUsername(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName())))
                .orElse(ResponseEntity.notFound().build());
    }
}
