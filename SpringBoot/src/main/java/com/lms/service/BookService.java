package com.lms.service;

import com.lms.entity.Book;
import com.lms.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingOrAuthorContainingOrIsbnContainingOrCategoryContaining(
           keyword, keyword, keyword, keyword
        );
    }

    public Book addBook(Book book) {
        book.setAvailable(true);
        return bookRepository.save(book);
    }

    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
