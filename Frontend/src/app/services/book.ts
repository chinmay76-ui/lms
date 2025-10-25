import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private baseUrl = 'http://localhost:8080/api/books';

  constructor(private http: HttpClient) {}

  getBooks(): Observable<any> {
    return this.http.get(`${this.baseUrl}/all`);
  }

  addBook(book: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/add`, book);
  }

  updateBook(book: any): Observable<any> {
  console.log('Sending update request with:', book);  // Add debug
  return this.http.put(`${this.baseUrl}/update`, book);
}


  deleteBook(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/delete/${id}`);
  }
}
