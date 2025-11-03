import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router'; // ✅ added

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule], // ✅ added RouterModule
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export default class Register {
  name: string = '';
  email: string = '';
  password: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  registerUser() {
    if (!this.name.trim() || !this.email.trim() || !this.password.trim()) {
      alert('Please fill in all fields');
      return;
    }

    const payload = {
      name: this.name.trim(),
      email: this.email.trim(),
      password: this.password.trim(),
      role: 'STUDENT' // ✅ Hardcoded, secure — cannot be changed from frontend
    };

    this.http.post('http://localhost:8080/api/users/register', payload, { responseType: 'text' })
      .subscribe({
        next: (response) => {
          if (response.includes('successful')) {
            alert('Registration successful! 🎉 Check your email for confirmation.');
            this.router.navigate(['/login']);
          } else {
            alert(response);
          }
        },
        error: (err) => {
          if (err.status === 400) alert('Email already exists');
          else alert('Server error. Please try again.');
        }
      });
  }
}
