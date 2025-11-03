package com.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.repository.UserRepository;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRepository;
import com.library.service.BorrowService;
import com.library.service.UserService;
import com.library.service.BookService;
import com.library.service.MailService;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    // ✅ Borrow request from frontend using userEmail + bookId + librarianEmail
    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestBorrowFrontend(@RequestBody BorrowRequestData data) {
        User user = userRepository.findByEmail(data.getUserEmail());
        Book book = bookRepository.findById(data.getBookId()).orElse(null);
        User librarian = data.getLibrarianEmail() != null
                ? userRepository.findByEmail(data.getLibrarianEmail())
                : null;

        if (user == null || book == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "❌ Invalid user or book details."));
        }

        // ✅ Check if the student already has a borrowed or pending record
        List<BorrowRecord> activeRecords = borrowRepository.findByUser(user);
        boolean hasActiveBorrow = activeRecords.stream()
                .anyMatch(r -> r.getStatus().equalsIgnoreCase("Borrowed") || r.getStatus().equalsIgnoreCase("Pending"));

        if (hasActiveBorrow) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "⚠️ You already have an active borrowed or pending book. Please return it first."));
        }

        if (book.getAvailableCopies() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "❌ Book not available for borrowing."));
        }

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));
        record.setStatus("Pending");

        if (librarian != null) {
            record.setApprovedBy(librarian);
        }

        borrowRepository.save(record);

        // ✅ Notify librarian
        String targetEmail = (librarian != null) ? librarian.getEmail() : "librarian@example.com";
        mailService.sendEmail(
                targetEmail,
                "New Borrow Request",
                "A new borrow request has been submitted by " + user.getName()
                        + " for the book \"" + book.getTitle() + "\"."
        );

        return ResponseEntity.ok(Map.of(
                "message", "✅ Borrow request submitted for \"" + book.getTitle() + "\". Awaiting librarian approval."
        ));
    }


     

    // ✅ DTO for frontend borrow request
    public static class BorrowRequestData {
        private String userEmail;
        private Long bookId;
        private String librarianEmail;

        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public String getLibrarianEmail() { return librarianEmail; }
        public void setLibrarianEmail(String librarianEmail) { this.librarianEmail = librarianEmail; }
    }

    // ✅ Borrow request via URL params (optional)
    @PostMapping("/{userId}/{bookId}")
    public String requestBorrow(@PathVariable Long userId, @PathVariable Long bookId) {
        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);

        if (user == null || book == null) {
            return "❌ Invalid user or book ID.";
        }

        if (book.getAvailableCopies() <= 0) {
            return "❌ Book not available for borrowing.";
        }

        borrowService.requestBorrow(user, book);
        return "✅ Borrow request submitted for \"" + book.getTitle() + "\". Awaiting librarian approval.";
    }

    // ✅ Return a borrowed book
    @PostMapping("/return/{recordId}")
    public String returnBook(@PathVariable Long recordId) {
        BorrowRecord record = borrowService.returnBook(recordId);
        if (record != null) {
            return "📚 " + record.getBook().getTitle() + " returned successfully!";
        } else {
            return "❌ Invalid record or already returned.";
        }
    }

    // ✅ Get all borrowed books for a specific user (by ID)
    @GetMapping("/user/{userId}")
    public List<BorrowRecord> getUserBorrowedBooks(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return borrowService.getBorrowedBooks(user);
    }

    // ✅ New: Fetch borrowed books by user email (for student "My Borrowed Books" page)
    @GetMapping("/user/email/{encodedEmail}")
    public ResponseEntity<?> getBorrowedBooksByUserEmail(@PathVariable String encodedEmail) {
        try {
            // Decode %40 to @
            String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
            User user = userRepository.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found for email: " + email));
            }

            List<BorrowRecord> borrowedBooks = borrowService.getBorrowedBooks(user);
            return ResponseEntity.ok(borrowedBooks);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error fetching borrowed books: " + e.getMessage()));
        }
    }

    // ✅ Get all pending borrow requests
    @GetMapping("/pending")
    public ResponseEntity<List<BorrowRecord>> getPendingRequests() {
        List<BorrowRecord> pendingRecords = borrowRepository.findByStatusIgnoreCase("Pending");
        return ResponseEntity.ok(pendingRecords);
    }

    // ✅ Get all approved borrow records
    @GetMapping("/approved")
    public List<BorrowRecord> getApprovedBorrowRecords() {
        return borrowRepository.findByStatusIgnoreCase("Borrowed");
    }

    // ✅ Approve borrow request
    @PostMapping("/approve/{recordId}/{librarianId}")
    public String approveBorrowRequest(@PathVariable Long recordId, @PathVariable Long librarianId) {
        User librarian = userService.getUserById(librarianId);
        borrowService.approveBorrow(recordId, librarian);
        return "✅ Borrow request approved successfully!";
    }

    // ✅ Reject borrow request
    @PostMapping("/reject/{recordId}/{librarianId}")
    public String rejectBorrowRequest(@PathVariable Long recordId, @PathVariable Long librarianId) {
        User librarian = userService.getUserById(librarianId);
        borrowService.rejectBorrow(recordId, librarian);
        return "❌ Borrow request rejected.";
    }

    // ✅ Send reminder email for overdue books
    @PostMapping("/reminder/{recordId}")
    public String sendReminder(@PathVariable Long recordId) {
        borrowService.sendReminder(recordId);
        return "✅ Reminder email sent successfully!";
    }
}
