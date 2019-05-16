import {Injectable} from '@angular/core';

import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {CookieService} from 'ngx-cookie-service';
import decode from 'jwt-decode';

import {BehaviorSubject, interval, Subscription} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {EnvHostService} from './env-host.service';

@Injectable()
export class AuthService {

  private static readonly CLIENT_ID = 'ui-service';
  private static readonly TOKEN_COOKIE_NAME = 'access_token';
  private static readonly REFRESH_TOKEN_COOKIE_NAME = 'refresh_token';
  private static readonly REFRESH_TOKEN_INTERVAL = 500 * 1000;

  public userInfoSubject = new BehaviorSubject({});
  private readonly TOKEN_ENDPOINT_URL = this.envHostService.getGatewayUrl() + 'auth-service/oauth/token';
  private refreshTokenSubscription: Subscription;

  constructor(private _http: HttpClient,
              private envHostService: EnvHostService,
              private cookieService: CookieService) {
  }


  public getToken = () => this.cookieService.get(AuthService.TOKEN_COOKIE_NAME);

  public isAuthenticated = () =>  this.cookieService.check(AuthService.TOKEN_COOKIE_NAME);

  public refreshUserInfo = () => this.userInfoSubject.next(this.getUserInfo());

  public logout = () => {
    this.cookieService.delete(AuthService.TOKEN_COOKIE_NAME, '/');
    this.cookieService.delete(AuthService.REFRESH_TOKEN_COOKIE_NAME, '/');
    if (this.refreshTokenSubscription) {
      this.refreshTokenSubscription.unsubscribe();
    }
  }

  public login = (username, password, onSuccess?, onError?) => {
    console.log('Trying to get token');
    const params = new HttpParams()
      .set('grant_type', 'password')
      .set('client_id', AuthService.CLIENT_ID)
      .set('username', username)
      .set('password', password);

    const headers = new HttpHeaders({
      'Access-Control-Allow-Headers': 'Content-Type, authorization',
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
      'Authorization': 'Basic ' + btoa(AuthService.CLIENT_ID + ':secret')
    });
    this._http.post(this.TOKEN_ENDPOINT_URL, params.toString(), {headers: headers})
      .subscribe(
        data => {
          this.saveToken(data);
          this.userInfoSubject.next(this.getUserInfo());
          this.initRefreshTokenJob();
          onSuccess();
        },
        err => {
          this.userInfoSubject.next({});
          console.error(err);
          onError();
        }
      );
  }

  private refreshToken = () => {
    const params = new HttpParams()
      .set('grant_type', 'refresh_token')
      .set('refresh_token', this.cookieService.get(AuthService.REFRESH_TOKEN_COOKIE_NAME));

    const headers = new HttpHeaders({
      'Access-Control-Allow-Headers': 'Content-Type, authorization',
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
      'Authorization': 'Basic ' + btoa(AuthService.CLIENT_ID + ':secret')
    });
    return this._http.post(this.TOKEN_ENDPOINT_URL, params.toString(), {headers: headers});
  }
  private saveToken = (token) => {
    const expireDate = new Date().getTime() + (1000 * token.expires_in);
    this.cookieService.set(AuthService.TOKEN_COOKIE_NAME, token.access_token, expireDate, '/');
    this.cookieService.set(AuthService.REFRESH_TOKEN_COOKIE_NAME, token.refresh_token, expireDate, '/');
  }
  private initRefreshTokenJob = () => {
    this.refreshTokenSubscription = interval(AuthService.REFRESH_TOKEN_INTERVAL).pipe(
      switchMap(_ => this.refreshToken())).subscribe(data => {
        console.log('success');
        this.saveToken(data);
      },
      err => {
        console.error(err);
      }
    );
  }
  private getUserInfo = () => {
    return decode(this.getToken());
  }



}
