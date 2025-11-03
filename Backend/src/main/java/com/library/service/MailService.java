package com.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Generic email sender (used internally)
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("library.lms.project@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    // ✅ Registration mail
    public void sendRegistrationMail(String to, String username) {
        String subject = "Welcome to Library Management System";
        String body = "Hello " + username + ",\n\n"
                + "🎉 Your Library Management System account has been created successfully!\n"
                + "You can now log in and start borrowing books.\n\n"
                + "Best Regards,\nLibrary Team 📚";
        sendEmail(to, subject, body);
    }

    // ✅ Borrow mail (Approved)
    public void sendBorrowMail(String to, String username, String bookTitle, String dueDate) {
        String subject = "✅ Borrow Request Approved";
        String body = "Hello " + username + ",\n\n"
                + "Your borrow request for the book \"" + bookTitle + "\" has been approved.\n"
                + "📅 Due Date: " + dueDate + "\n\n"
                + "Please collect and return the book on time to avoid penalties.\n\n"
                + "Happy Reading!\nLibrary Team 📚";
        sendEmail(to, subject, body);
    }

    // ✅ Rejection mail
    public void sendRejectionMail(String to, String username, String bookTitle) {
        String subject = "❌ Borrow Request Rejected";
        String body = "Hello " + username + ",\n\n"
                + "We regret to inform you that your borrow request for the book \"" + bookTitle + "\" "
                + "has been rejected by the librarian.\n\n"
                + "You may try borrowing a different book or contact the library for more details.\n\n"
                + "Regards,\nLibrary Team 📚";
        sendEmail(to, subject, body);
    }

    // ✅ Return mail
    public void sendReturnMail(String to, String username, String bookTitle) {
        String subject = "📘 Book Returned Successfully";
        String body = "Hello " + username + ",\n\n"
                + "Thank you for returning the book \"" + bookTitle + "\".\n"
                + "We hope you enjoyed reading it!\n\n"
                + "Regards,\nLibrary Team 📚";
        sendEmail(to, subject, body);
    }

    // ✅ Manual due-date / overdue reminder mail
    public void sendDueReminderMail(String to, String username, String bookTitle, String dueDate, boolean overdue) {
        String subject;
        String body;

        if (overdue) {
            subject = "⏰ Overdue Book Reminder";
            body = "Hello " + username + ",\n\n"
                    + "The book \"" + bookTitle + "\" was due on " + dueDate + ".\n"
                    + "Please return it as soon as possible to avoid further penalties.\n\n"
                    + "Regards,\nLibrary Team 📚";
        } else {
            subject = "📅 Upcoming Due Date Reminder";
            body = "Hello " + username + ",\n\n"
                    + "Just a reminder: The book \"" + bookTitle + "\" you borrowed is due on " + dueDate + ".\n"
                    + "Please ensure it’s returned on or before the due date.\n\n"
                    + "Happy Reading!\nLibrary Team 📚";
        }

        sendEmail(to, subject, body);
    }
}
