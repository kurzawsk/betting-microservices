import {Injectable} from '@angular/core';
import {AuthService} from './auth.service';
import {ActivatedRouteSnapshot, Router} from '@angular/router';
import decode from 'jwt-decode';

@Injectable()
export class RoleGuard {

  private getAuthorities = () => {
    console.log('token info = ' + JSON.stringify(decode(this.authService.getToken())));
    const authorities = decode(this.authService.getToken()).authorities;
    if (Array.isArray(authorities)) {
      return authorities;
    } else if (authorities) {
      return [authorities];
    } else {
      return [];
    }
  }

  constructor(public authService: AuthService, public router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot): boolean {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['login']);
      return false;
    } else if (route.data && route.data.expectedRole && !this.getAuthorities().includes(route.data.expectedRole)) {
      this.router.navigate(['main']);
      return false;
    } else if (route.data && route.data.expectedRole && route.data.exp < Math.floor((new Date).getTime() / 1000)) {
      console.log('RoleGuard - token expired, exp date is: ' + new Date(route.data.exp));
      this.router.navigate(['main']);
      return false;
    }
    return true;
  }
}
