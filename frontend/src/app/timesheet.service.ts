import { Injectable } from '@angular/core';
import {Timesheet} from './timesheets';
import {Student} from './students';
import {LoginData} from './loginData';

import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams, HttpErrorResponse} from '@angular/common/http';
import {map} from "rxjs/operators";
import { Location } from '@angular/common';


@Injectable({
  providedIn: 'root'
})
export class TimesheetService {
  loginData: LoginData;
  message: string;
  url: string= 'https://'+window.location.hostname+':8080/';

  constructor(private http: HttpClient) { }


  getStudents(year: number, month: number): Observable<Timesheet[]>{
    return this.http.get<Timesheet[]>(this.url+'timesheets/'+year+'/'+month);
    //return this.http.get<Timesheet[]>('https://qgm2xbvh9d.execute-api.eu-central-1.amazonaws.com/default/'+year+"/"+month);
  };

  createTimesheet(userName: string, year: number, month: number): Observable<any> {
      return this.http.put<any>(this.url+'timesheets/'+userName+'/'+year+'/'+month,
        JSON.parse('{"loginUserName": "'+sessionStorage.getItem('loginUserName')+'", "password": "'+sessionStorage.getItem('password')+'"}'))//, {'Content-Type': 'application/json'});
  }

  openTimesheet(userName: string, year: number, month: number){
      return this.http.get(this.url+'timesheets/'+userName+'/'+year+'/'+month,
       {
         responseType: 'blob',
         observe: 'response',
         headers: {'Accept': 'application/octet-stream'}
         });
  }

  postNewStudent(studentData): Observable<string>{
     const body= JSON.parse('{"firstName": "'+studentData.firstName+'", "lastName": "'+studentData.lastName+'", "userName": "'+studentData.userName+'"}');
     const params:HttpParams= new HttpParams().set('loginUserName', sessionStorage.getItem('loginUserName')).set('password', sessionStorage.getItem('password'));
    //return this.http.post("https://h4iq8owtoj.execute-api.eu-central-1.amazonaws.com/default", body, {responseType: "text", params: params});
    return this.http.post(this.url+"students", body, {responseType: "text", params: params});
  }

  deleteStudent(student: Student){
  return this.http.delete(this.url+"students/"+student.userName, {responseType: 'text'});
    //return this.http.delete('https://h4iq8owtoj.execute-api.eu-central-1.amazonaws.com/default/'+student.userName, {responseType: 'text'});
  }

  deleteTimesheet(timesheet: Timesheet){
    return this.http.delete(this.url+'timesheets/'+timesheet.student.userName, {responseType: 'text'});
  }

  /*postUserInCognito(token: string){
    const body=JSON.parse('{"token": "'+token+'"}')
    return this.http.post('https:localhost:8080/cognito',body);
  }*/


}
