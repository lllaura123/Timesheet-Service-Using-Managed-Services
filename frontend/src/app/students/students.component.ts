import { Component, OnInit } from '@angular/core';
import { Student } from '../students';
import { StudentService } from '../student.service';
import { HttpHeaders} from '@angular/common/http';

@Component({
  selector: 'app-students',
  templateUrl: './students.component.html',
  styleUrls: ['./students.component.css']
})
export class StudentsComponent implements OnInit {
  students: Student[];
  date: Date = new Date();
  monthName: string;

  constructor(private studentService: StudentService) { }

  ngOnInit(): void {
    this.date.setMonth(this.date.getMonth()-1);
    this.monthName= this.displayMonth();
    this.getStudents(this.date.getFullYear(), this.date.getMonth()+1);
  }

  getLastMonth() {
    this.date.setMonth(this.date.getMonth()-1);
    this.getStudents(this.date.getFullYear(), this.date.getMonth()+1);
    this.monthName= this.displayMonth();
    }

  getNextMonth() {
    this.date.setMonth(this.date.getMonth()+1);
    this.getStudents(this.date.getFullYear(), this.date.getMonth()+1);
    this.monthName= this.displayMonth();
  }

  getStudents(year: number, month: number): void {
    this.studentService.getStudents(year, month)
      .subscribe(res=> {this.students= res
        }, error => {
          if (error.status==0) alert("Connection refused");
          });
  }

  createExcel(student: Student){
    this.studentService.createTimesheet(student.userName, this.date.getFullYear(), this.date.getMonth()+1)
      .subscribe(responseData => {
        student.filePath= responseData.filePath;
        student.fileExists= responseData.fileExists;
        console.log("Created File on "+student.filePath);
      }, error => {
        if (error.status==0)alert("Connection refused");
        else if (error.status==404) alert("Bad Request");
        else if (error.status==500) alert("Error on Server Side, File could not be created");
      });
  }

  openExcel(student: Student) {
    this.studentService.openTimesheet(student.userName, this.date.getFullYear(), this.date.getMonth()+1)
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
         if (error.status==0)alert("Connection refused");
         else if (error.status==404) alert("Bad Request");
         else if (error.status==500) alert("Error on server side, file could not be downloaded");
        console.log('download error');
      }, () => {
        console.log('Completed file download.')
      });
  }

 /* createExcel(student: Student){
    let body= this.createFormData(student);
    this.studentService.createTimesheet(body)
      .subscribe(responseData => {
        student.filePath= responseData.filePath;
        student.fileExists= responseData.fileExists;
        console.log("Created File on "+student.filePath);
      }, error => {
        if (error.status==0)alert("Connection refused");
        else if (error.status==404) alert("Bad Request");
        else if (error.status==500) alert("Error on Server Side, File could not be created");
      });
      }*/

/*    openExcel(student: Student) {
    const body= new FormData();
    body.append('lastName', student.lastName);
    body.append('month', this.dateToString(this.date));
    this.studentService.openTimesheet(student.lastName, this.dateToString(this.date))
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
         if (error.status==0)alert("Connection refused");
         else if (error.status==404) alert("Bad Request");
         else if (error.status==500) alert("Error on server side, file could not be downloaded");
        console.log('download error');
      }, () => {
        console.log('Completed file download.')
      });

  }*/

/*  dateToString(date: Date): string {
    let year: number = date.getFullYear();
    let month: number = date.getMonth()+1;
    return year+"-"+((month>9)?month: "0"+(month))+"-01";
  }*/

  displayMonth(): string {
    let monthNames= ["Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August","September", "Oktober", "November", "Dezember"];
    return monthNames[this.date.getMonth()]+" "+ this.date.getFullYear();
  }


 /* createFormData(student: Student){
     const body= new FormData();
     body.append('firstName', student.firstName);
     body.append('lastName', student.lastName)
     body.append('link', student.link);
     body.append('month', this.dateToString(this.date));
     return body;
  }*/
}
