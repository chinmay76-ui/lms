import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import Home from './components/home/home';
import Register from './components/register/register';
import Dashboard from './components/dashboard/dashboard';
import Borrow from './components/borrow/borrow';
import Return from './components/return/return';
import AdminSummary from './components/admin-summary/admin-summary';
import LibrarianDashboard from './components/librarian-dashboard/librarian-dashboard';
import Student from './student/student';
import { authGuard } from './guards/auth.guard'; // ✅ import guard

export const routes: Routes = [
  // ✅ Public routes
  { path: '', component: Login },
  { path: 'login', component: Login },
  { path: 'register', component: Register },

  // ✅ Protected routes (with session + role control)
  { path: 'home', component: Home, canActivate: [authGuard] },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard], data: { role: 'STUDENT' } },
  { path: 'borrow', component: Borrow, canActivate: [authGuard], data: { role: 'STUDENT' } },
  { path: 'return', component: Return, canActivate: [authGuard], data: { role: 'STUDENT' } },
  { path: 'admin-summary', component: AdminSummary, canActivate: [authGuard], data: { role: 'ADMIN' } },
  { path: 'librarian-dashboard', component: LibrarianDashboard, canActivate: [authGuard], data: { role: 'LIBRARIAN' } },
  { path: 'student', component: Student, canActivate: [authGuard], data: { role: 'STUDENT' } },

  // ✅ Fallback route
  { path: '**', redirectTo: 'login' }
];
