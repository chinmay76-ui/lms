import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BookService } from '../services/book';

@Component({
  selector: 'app-book-add',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './book-add.html',
  styleUrl: './book-add.css'
})
export class BookAddComponent implements OnInit {
  book: any = {
    id: null,
    title: '',
    author: '',
    isbn: '',
    category: '',
    quantity: 0,
    available: true  // Added available field
  };
  isEditMode: boolean = false;

  constructor(
    private bookService: BookService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.loadBook(params['id']);
      }
    });
  }

  loadBook(id: number): void {
    this.bookService.getBooks().subscribe({
      next: (books) => {
        const foundBook = books.find((b: any) => b.id == id);
        if (foundBook) {
          this.book = { ...foundBook };
        }
      }
    });
  }

  saveBook(): void {
    if (this.isEditMode) {
      this.bookService.updateBook(this.book).subscribe({
        next: () => {
          this.router.navigate(['/books']);
        },
        error: (err) => {
          console.error('Error updating book:', err);
          alert('Failed to update book');
        }
      });
    } else {
      this.bookService.addBook(this.book).subscribe({
        next: () => {
          this.router.navigate(['/books']);
        },
        error: (err) => {
          console.error('Error adding book:', err);
          alert('Failed to add book');
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/books']);
  }
}
