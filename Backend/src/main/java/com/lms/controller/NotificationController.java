package com.lms.controller;

import com.lms.entity.Notification;
import com.lms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService service;

    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        return service.getUserNotifications(userId);
    }

    @PostMapping("/send")
    public Notification sendNotification(@RequestParam Long userId, @RequestParam String message) {
        return service.sendNotificationToUser(userId, message);
    }
}
