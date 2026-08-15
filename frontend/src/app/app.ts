import {
  Component,
  computed,
  signal
} from '@angular/core';

import {
  NavigationEnd,
  Router,
  RouterOutlet
} from '@angular/router';

import { filter } from 'rxjs';

import { Topbar } from './shared/topbar/topbar';


@Component({
  selector: 'app-root',

  standalone: true,

  imports: [
    RouterOutlet,
    Topbar
  ],

  templateUrl: './app.html',

  styleUrl: './app.scss'
})
export class App {

  protected readonly title =
    signal('frontend');


  private readonly currentUrl =
    signal('');


  protected readonly showTopbar =
    computed(() => {

      const url =
        this.currentUrl();


      return !(
        url.startsWith('/login') ||
        url.startsWith('/forgot-password') ||
        url.startsWith('/reset-password') ||
        url.startsWith('/change-password')
      );
    });


  constructor(
    private router: Router
  ) {

    this.currentUrl.set(
      this.router.url
    );


    this.router.events
      .pipe(
        filter(
          event =>
            event instanceof NavigationEnd
        )
      )
      .subscribe(
        event => {

          this.currentUrl.set(
            event.urlAfterRedirects
          );
        }
      );
  }
}