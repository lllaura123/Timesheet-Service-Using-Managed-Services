import { Injectable } from '@angular/core';
import {Timesheet} from './timesheets';
import {Student} from './students';
import {LoginData} from './loginData';

import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams, HttpErrorResponse} from '@angular/common/http';
import {map} from "rxjs/operators";


@Injectable({
  providedIn: 'root'
})
export class TimesheetService {
  loginData: LoginData;


  constructor(private http: HttpClient) { }


  getStudents(year: number, month: number): Observable<Timesheet[]>{
      return this.http.get<Timesheet[]>('https://localhost:8080/timesheets/'+year+"/"+month);
  };

  createTimesheet(userName: string, year: number, month: number): Observable<Timesheet> {
      return this.http.put<any>('https://localhost:8080/timesheets/'+userName+'/'+year+'/'+month,
        JSON.parse('{"loginUserName": "'+sessionStorage.getItem('loginUserName')+'", "password": "'+sessionStorage.getItem('password')+'"}'));
  }

  openTimesheet(userName: string, year: number, month: number){
      return this.http.get('https://localhost:8080/timesheets/'+userName+'/'+year+'/'+month,
       {
         responseType: 'blob',
         observe: 'response',
         headers: new HttpHeaders({
         'Access-Control-Allow-Origin': '*'
       })});
  }

  postNewStudent(studentData){
     const body= new FormData();
     body.append('firstName', studentData.firstName);
     body.append('lastName', studentData.lastName);
     body.append('userName', studentData.userName);
     body.append('loginUserName', sessionStorage.getItem('loginUserName'));
     body.append('password', sessionStorage.getItem('password'));
    return this.http.post<any>('https://localhost:8080/students', body);
  }

  deleteStudent(student: Student){
    return this.http.delete('https://localhost:8080/students/'+student.userName, {responseType: 'text'});
  }

  deleteTimesheet(timesheet: Timesheet){
    return this.http.delete('https://localhost:8080/timesheets/'+timesheet.student.userName+'/'+timesheet.year+'/'+ timesheet.month, {responseType: 'text'});
  }

/*  validateLogin(loginData:LoginData){
    const params:HttpParams= new HttpParams().set('loginUserName', loginData.loginUserName).set('password', loginData.password);
    return this.http.get("https://localhost:8080/login", {responseType: 'text', params: params});
  }

  getStudents(date: string): Observable<Student[]>{
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
