import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-librarian-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './librarian-dashboard.html',
  styleUrls: ['./librarian-dashboard.css']
})
export default class LibrarianDashboard implements OnInit {
  books: any[] = [];
  pendingRequests: any[] = [];
  approvedRecords: any[] = [];
  message = '';
  newBook = { title: '', author: '', totalCopies: 1, availableCopies: 1 };
  editingBook: any = null;
  librarianId: number = 0;
  sentReminders: Set<number> = new Set();

  private bookApi = 'http://localhost:8080/api/books';
  private borrowApi = 'http://localhost:8080/api/borrow';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    const userData = sessionStorage.getItem('user');
    if (userData) {
      const user = JSON.parse(userData);
      this.librarianId = user.id;
    }

    this.loadBooks();
    this.loadPendingRequests();
    this.loadApprovedRequests();
  }

  // 📚 Books management
  loadBooks() {
    this.http.get(this.bookApi).subscribe((data: any) => (this.books = data));
  }

  addBook() {
    this.http.post(this.bookApi, this.newBook).subscribe({
      next: () => {
        this.message = '✅ Book added successfully!';
        this.newBook = { title: '', author: '', totalCopies: 1, availableCopies: 1 };
        this.loadBooks();
      },
      error: () => (this.message = '❌ Failed to add book')
    });
  }

  editBook(book: any) {
    this.editingBook = { ...book };
  }

  cancelEdit() {
    this.editingBook = null;
  }

  updateBook() {
    this.http.put(`${this.bookApi}/${this.editingBook.id}`, this.editingBook).subscribe({
      next: () => {
        this.message = '✅ Book updated successfully!';
        this.editingBook = null;
        this.loadBooks();
      },
      error: () => (this.message = '❌ Failed to update book')
    });
  }

  deleteBook(id: number) {
    this.http.delete(`${this.bookApi}/${id}`).subscribe({
      next: () => {
        this.message = '✅ Book deleted!';
        this.loadBooks();
      },
      error: () => (this.message = '❌ Failed to delete book')
    });
  }

  // 🕓 Borrow requests
  loadPendingRequests() {
    this.http.get(`${this.borrowApi}/pending`).subscribe((data: any) => (this.pendingRequests = data));
  }

  loadApprovedRequests() {
    this.http.get(`${this.borrowApi}/approved`).subscribe((data: any) => (this.approvedRecords = data));
  }

  // ✅ Approve Request
  approveRequest(recordId: number) {
    this.http.post(`${this.borrowApi}/approve/${recordId}/${this.librarianId}`, {}, { responseType: 'text' }).subscribe({
      next: (res: string) => {
        this.message = res || '✅ Borrow request approved!';
        setTimeout(() => {
          this.loadPendingRequests();
          this.loadApprovedRequests();
        }, 500);
      },
      error: () => (this.message = '❌ Failed to approve request')
    });
  }

  // ❌ Reject Request
  reject(recordId: number) {
    this.http.post(`${this.borrowApi}/reject/${recordId}/${this.librarianId}`, {}, { responseType: 'text' }).subscribe({
      next: (res: string) => {
        this.message = res || '❌ Borrow request rejected!';
        setTimeout(() => this.loadPendingRequests(), 500);
      },
      error: () => (this.message = '❌ Failed to reject request')
    });
  }

  // ✉️ Manual Reminder Mail
  sendManualReminder(recordId: number) {
    const record = this.approvedRecords.find(r => r.id === recordId);
    if (!record || !record.user?.email) {
      alert('❌ No email found for this record.');
      return;
    }

    if (this.sentReminders.has(recordId)) {
      alert('⚠️ Reminder already sent recently.');
      return;
    }

    this.http.post(`http://localhost:8080/api/borrow/reminder/${recordId}`, {}, { responseType: 'text' }).subscribe({
      next: (response: string) => {
        alert(response); // Displays "✅ Reminder email sent successfully!"
        this.sentReminders.add(recordId);
      },
      error: (err) => {
        console.error(err);
        alert('❌ Failed to send reminder');
      }
    });
  }
}
