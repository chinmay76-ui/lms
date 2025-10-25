package com.lms.repository;

import com.lms.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingOrAuthorContainingOrIsbnContainingOrCategoryContaining(
        String title, String author, String isbn, String category
    );
}
