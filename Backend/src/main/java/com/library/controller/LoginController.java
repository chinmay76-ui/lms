package com.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.library.entity.User;
import com.library.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ✅ Login user and create session
    @PostMapping("/session-login")
    public String loginUser(@RequestBody User loginRequest, HttpSession session) {
        User user = userRepository.findByEmail(loginRequest.getEmail());

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            session.setAttribute("user", user);
            System.out.println("✅ Session created for: " + user.getEmail() + " (" + user.getRole() + ")");
            return "✅ Logged in as " + user.getRole();
        } else {
            System.out.println("❌ Invalid login attempt for: " + loginRequest.getEmail());
            return "❌ Invalid email or password";
        }
    }

    // ✅ Get current session user
    @GetMapping("/me")
    public User getCurrentUser(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        System.out.println("🔍 Checking session user: " + (currentUser != null ? currentUser.getEmail() : "null"));
        return currentUser;
    }

    // ✅ Logout user and destroy session
    @PostMapping("/logout")
    public String logoutUser(HttpSession session) {
        session.invalidate();
        System.out.println("🚪 Session invalidated.");
        return "🚪 Logged out successfully";
    }
}
