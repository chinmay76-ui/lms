import { Routes } from '@angular/router';
import { LoginComponent } from './login/login';
import { BookListComponent } from './book-list/book-list';
import { BookAddComponent } from './book-add/book-add';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'books', component: BookListComponent },
  { path: 'book-add', component: BookAddComponent }
];
