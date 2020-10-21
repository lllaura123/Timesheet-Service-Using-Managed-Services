import { Injectable } from '@angular/core';
import { Student } from './students';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams, HttpErrorResponse} from '@angular/common/http';
import {map} from "rxjs/operators";


@Injectable({
  providedIn: 'root'
})
export class StudentService {

  constructor(private http: HttpClient) { }


  getStudents(year: number, month: number): Observable<Student[]>{
      return this.http.get<Student[]>('https://localhost:8084/students/'+year+"/"+month)
  };

  createTimesheet(userName: string, year: number, month: number): Observable<Student> {
      return this.http.put<any>('https://localhost:8084/students/'+userName+'/timesheets/'+year+'/'+month, null);
  }

  openTimesheet(userName: string, year: number, month: number){
      return this.http.get('https://localhost:8084/students/'+userName+'/timesheets/'+year+'/'+month,
       {
         responseType: 'blob',
         observe: 'response',
         headers: new HttpHeaders({
         'Access-Control-Allow-Origin': '*'
       })});
  }




/*  getStudents(date: string): Observable<Student[]>{
    return this.http.get<Student[]>('https://localhost:8084/api/checklist',
    {
      params: new HttpParams().set('month', date)
    });
  }
  createTimesheet(data): Observable<Student> {
      return this.http.post<any>('https://localhost:8084/api/checklist/create', data);
  }

  openTimesheet(lastName:string, month: string){
      return this.http.get('https://localhost:8084/api/checklist/open',
       {
         params: new HttpParams().set('month', month).set('lastName', lastName),
         responseType: 'blob',
         observe: 'response',
         headers: new HttpHeaders({
         'Access-Control-Allow-Origin': '*'
       })});
        }
*/
}
