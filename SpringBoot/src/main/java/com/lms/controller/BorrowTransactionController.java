package com.lms.controller;

import com.lms.entity.BorrowTransaction;
import com.lms.service.BorrowTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class BorrowTransactionController {

    @Autowired
    private BorrowTransactionService service;

    @PostMapping("/borrow")
    public BorrowTransaction borrow(@RequestParam Long bookId, @RequestParam Long userId) {
        return service.borrowBook(bookId, userId);
    }

    @PostMapping("/approve")
    public BorrowTransaction approve(@RequestParam Long transactionId) {
        return service.approveBorrow(transactionId);
    }

    @PostMapping("/return")
    public BorrowTransaction returnBook(@RequestParam Long transactionId) {
        return service.returnBook(transactionId);
    }

    @GetMapping("/history/{userId}")
    public List<BorrowTransaction> getHistory(@PathVariable Long userId) {
        return service.getUserHistory(userId);
    }
}
