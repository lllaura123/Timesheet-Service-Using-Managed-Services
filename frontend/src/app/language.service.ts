import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpErrorResponse} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {

  url: string= 'https://'+window.location.hostname+':8080/lang/';

  constructor(private http: HttpClient) { }

  putLanguage(lang: string){
      return this.http.put(this.url+lang, "", {responseType: 'text'});
  }
}
