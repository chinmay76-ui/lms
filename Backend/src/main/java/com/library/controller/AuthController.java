package com.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.library.entity.User;
import com.library.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserService userService;

    // ✅ User Login (with BCrypt)
    @PostMapping("/login")
    public User login(@RequestBody User loginRequest) {
        User user = userService.validateLogin(loginRequest.getEmail(), loginRequest.getPassword());
        if (user != null) {
            System.out.println("✅ Login successful for: " + user.getEmail());
            return user;
        }
        System.out.println("❌ Invalid login attempt for email: " + loginRequest.getEmail());
        return null; // You can also throw custom exception if needed
    }

    // ✅ User Registration (Student or Librarian)
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.registerUser(user);
    }
}
