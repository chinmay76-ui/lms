import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {
  email: string = '';
  password: string = '';
  isLoading: boolean = false;

  constructor(private http: HttpClient, private router: Router) {}

  loginUser() {
    if (!this.email.trim() || !this.password.trim()) {
      alert('Please enter both email and password');
      return;
    }

    this.isLoading = true;

    this.http.post<any>('http://localhost:8080/api/users/login', {
      email: this.email.trim(),
      password: this.password.trim()
    }).subscribe({
      next: (user) => {
        this.isLoading = false;

        if (!user || !user.role) {
          alert('Invalid response from server.');
          return;
        }

        console.log('Login response:', user); // 👀 For debugging
        alert(`Welcome ${user.name}! You are logged in as ${user.role}.`);

        // ✅ Save user details in sessionStorage
        sessionStorage.setItem('user', JSON.stringify(user));
        sessionStorage.setItem('userEmail', user.email);
        sessionStorage.setItem('userRole', user.role);
        sessionStorage.setItem('userName', user.name);

        // ✅ Role-based redirect (safe with case handling)
        const role = user.role ? user.role.toUpperCase() : '';

        switch (role) {
          case 'ADMIN':
            this.router.navigate(['/admin-summary']);
            break;
          case 'LIBRARIAN':
            this.router.navigate(['/librarian-dashboard']); // ✅ fixed path
            break;
          default:
            this.router.navigate(['/home']);
        }
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 401) {
          alert('Invalid credentials');
        } else {
          alert('Server error. Please try again.');
        }
      }
    });
  }
}
