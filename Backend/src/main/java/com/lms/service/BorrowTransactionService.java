package com.lms.service;

import com.lms.entity.BorrowTransaction;
import com.lms.entity.Book;
import com.lms.entity.User;
import com.lms.repository.BorrowTransactionRepository;
import com.lms.repository.BookRepository;
import com.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowTransactionService {

    @Autowired
    private BorrowTransactionRepository transactionRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public BorrowTransaction borrowBook(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        BorrowTransaction transaction = new BorrowTransaction();
        transaction.setBook(book);
        transaction.setUser(user);
        transaction.setBorrowDate(LocalDate.now());
        transaction.setDueDate(LocalDate.now().plusDays(14)); // Example: 14 day loan
        transaction.setApproved(false);
        transaction.setReturned(false);

        book.setAvailable(false);
        bookRepository.save(book);

        return transactionRepository.save(transaction);
    }

    public BorrowTransaction approveBorrow(Long transactionId) {
        BorrowTransaction transaction = transactionRepository.findById(transactionId).orElseThrow();
        transaction.setApproved(true);
        return transactionRepository.save(transaction);
    }

    public BorrowTransaction returnBook(Long transactionId) {
        BorrowTransaction transaction = transactionRepository.findById(transactionId).orElseThrow();
        transaction.setReturned(true);
        transaction.setReturnDate(LocalDate.now());
        Book book = transaction.getBook();
        book.setAvailable(true);
        bookRepository.save(book); // update availability
        return transactionRepository.save(transaction);
    }

    public List<BorrowTransaction> getUserHistory(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
}
