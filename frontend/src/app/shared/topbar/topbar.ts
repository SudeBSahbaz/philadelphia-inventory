import { Component } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterLinkActive
} from '@angular/router';

import { AuthService } from '../../core/services/auth.service';


@Component({
  selector: 'app-topbar',

  standalone: true,

  imports: [
    RouterLink,
    RouterLinkActive
  ],

  templateUrl: './topbar.html',

  styleUrl: './topbar.scss'
})
export class Topbar {

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}


  isAdmin(): boolean {

    return this.authService.hasRole(
      'ADMIN'
    );
  }


  logout(): void {

    this.authService
      .logout()
      .subscribe({

        next: () => {

          this.router.navigate([
            '/login'
          ]);
        },

        error: () => {

          this.router.navigate([
            '/login'
          ]);
        }
      });
  }
}