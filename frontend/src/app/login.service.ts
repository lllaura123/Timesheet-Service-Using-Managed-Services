import { Injectable } from '@angular/core';
import {Student} from './students';
import {LoginData} from './loginData';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams, HttpErrorResponse} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  loginData: LoginData;
  url: string= 'https://'+window.location.hostname+':8080/login';

  constructor(private http: HttpClient) { }

  validateLogin(loginData:LoginData){
    const params:HttpParams= new HttpParams().set('loginUserName', loginData.loginUserName).set('password', loginData.password);
    return this.http.get(this.url,
      {responseType: 'text',
      params: params,
});
  }
}
