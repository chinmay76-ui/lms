import { Component } from '@angular/core';
import { Router, RouterOutlet, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  template: `
    <nav *ngIf="showNav" class="nav-bar">
      <!-- 👇 Student menu -->
      <ng-container *ngIf="userRole === 'STUDENT'">
        <a routerLink="/home">Home</a> |
        <a routerLink="/student">Available Books</a> |
        <a routerLink="/borrow">My Borrowed Books</a> |
      </ng-container>

      <!-- 👇 Librarian menu -->
      <ng-container *ngIf="userRole === 'LIBRARIAN'">
        <a routerLink="/librarian-dashboard">Librarian Dashboard</a> |
      </ng-container>

      <!-- 👇 Admin menu -->
      <ng-container *ngIf="userRole === 'ADMIN'">
        <a routerLink="/admin-summary">Admin Summary</a> |
      </ng-container>

      <!-- 👇 Logout visible to all -->
      <a (click)="logout()" style="cursor:pointer; color:red;">Logout</a>
    </nav>

    <router-outlet></router-outlet>
  `,
  styles: [`
    .nav-bar {
      background-color: #f5f5f5;
      padding: 10px;
    }
    a {
      margin-right: 10px;
      text-decoration: none;
      color: #333;
    }
    a:hover {
      text-decoration: underline;
    }
  `]
})
export class App {
  showNav = false;
  userRole: string | null = null;

  constructor(private router: Router) {
    // ✅ Detect route changes and update navbar visibility + role
    this.router.events.subscribe(() => {
      const hiddenRoutes = ['/', '/login', '/register'];
      this.showNav = !hiddenRoutes.includes(this.router.url);

      const storedRole = sessionStorage.getItem('userRole');
      this.userRole = storedRole ? storedRole.toUpperCase() : null;
    });
  }

  logout() {
    // ✅ Clear session and navigate to login
    sessionStorage.clear();
    this.userRole = null;
    this.showNav = false;
    this.router.navigate(['/login']);
    alert('You have been logged out.');
  }
}
