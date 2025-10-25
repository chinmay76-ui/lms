import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { BookService } from '../services/book';
import { AuthService } from '../services/auth';

@Component({
  selector: 'app-book-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './book-list.html',
  styleUrl: './book-list.css'
})
export class BookListComponent implements OnInit {
  books: any[] = [];
  selectedBook: any = null;
  showDeleteConfirm: boolean = false;

  constructor(
    private bookService: BookService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.bookService.getBooks().subscribe({
      next: (data) => {
        this.books = data;
      },
      error: (err) => {
        console.error('Error loading books:', err);
      }
    });
  }

  addBook(): void {
    this.router.navigate(['/book-add']);
  }

  editBook(book: any): void {
    this.router.navigate(['/book-add'], { queryParams: { id: book.id } });
  }

  deleteBook(book: any): void {
    this.selectedBook = book;
    this.showDeleteConfirm = true;
  }

  confirmDelete(): void {
    if (this.selectedBook) {
      this.bookService.deleteBook(this.selectedBook.id).subscribe({
        next: () => {
          this.loadBooks();
          this.cancelDelete();
        },
        error: (err) => {
          console.error('Error deleting book:', err);
          alert('Failed to delete book');
        }
      });
    }
  }

  cancelDelete(): void {
    this.selectedBook = null;
    this.showDeleteConfirm = false;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
