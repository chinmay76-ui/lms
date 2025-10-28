import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login(): void {
    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        this.authService.saveToken(response.token);

        // Decode the JWT and extract the role(s)
        const decoded: any = jwtDecode(response.token);
        // Check for role property (customize the key if your backend uses another name)
        let role = '';
        if (decoded.role) role = decoded.role;
        else if (decoded.roles) role = Array.isArray(decoded.roles) ? decoded.roles[0] : decoded.roles;
        else if (decoded.authorities) role = Array.isArray(decoded.authorities) ? decoded.authorities[0] : decoded.authorities;

        localStorage.setItem('role', role);
        this.router.navigate(['/books']);
      },
      error: () => {
        this.error = 'Invalid username or password';
      }
    });
  }
}
