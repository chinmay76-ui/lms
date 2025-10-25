package com.lms.service;

import com.lms.entity.Notification;
import com.lms.entity.User;
import com.lms.repository.NotificationRepository;
import com.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public Notification sendNotificationToUser(Long userId, String message) {
        User user = userRepository.findById(userId).orElseThrow();
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setSentAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
}
