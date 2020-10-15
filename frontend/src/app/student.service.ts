import { Injectable } from '@angular/core';
import { Student } from './students';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import {map} from "rxjs/operators";


@Injectable({
  providedIn: 'root'
})
export class StudentService {

  constructor(private http: HttpClient) { }

  getStudents(date: string): Observable<Student[]>{
    return this.http.get<Student[]>('https://localhost:8084/api/checklist',
    {
      params: new HttpParams().set('month', date)
    });
  }
  createTimesheet(data): Observable<Student> {
         return this.http.post<any>('https://localhost:8084/api/checklist/create', data);
       }

  openTimesheet(data){
         return this.http.post('https://localhost:8084/api/checklist/open', data, {
           responseType:'blob',
           observe: 'response',
           headers: new HttpHeaders({
            'Access-Control-Allow-Origin': '*'
          })})
         .subscribe(res => {
            var headers: HttpHeaders= res.headers;
            var filename= headers.get('fileName');
            console.log('start download:',res);
            var url = window.URL.createObjectURL(res.body);
            console.log(url);
            var a = document.createElement('a');
            document.body.appendChild(a);
            a.setAttribute('style', 'display: none');
            a.href = url;
            a.download= filename;
            a.click();
            window.URL.revokeObjectURL(url);
            a.remove(); // remove the element
          }, error => {
            console.log('download error');
          }, () => {
            console.log('Completed file download.')
          });
      }

}
