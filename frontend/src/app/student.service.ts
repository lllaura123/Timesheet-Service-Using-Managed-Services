import { Injectable } from '@angular/core';
import { Student } from './students';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})
export class StudentService {

  constructor(private http: HttpClient) { }

  getStudents(date: string): Observable<Student[]>{
    return this.http.get<Student[]>('http://localhost:8080/api/checklist',
    {
      params: new HttpParams().set('month', date)
    });
  }
}
