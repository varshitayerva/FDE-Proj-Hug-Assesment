package com.aistyle.controller;

import com.aistyle.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AuthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Auth service is healthy", "OK", 200));
    }
}
