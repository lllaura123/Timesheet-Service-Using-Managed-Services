import { Component, OnInit, Input } from '@angular/core';
import { Timesheet } from '../timesheets';
import {LoginData} from '../loginData';
import { TimesheetService } from '../timesheet.service';

import { HttpHeaders} from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
//import {SESSION_STORAGE, WebStorageService} from 'angular-webstorage-service';

@Component({
  selector: 'app-timesheets',
  templateUrl: './timesheets.component.html',
  styleUrls: ['./timesheets.component.css']
})
export class TimesheetsComponent implements OnInit {
  timesheets: Timesheet[];
  date: Date = new Date();
  monthName: string;
  inputForm;
  dialog: boolean= false;
  selectedTimesheet: Timesheet;
  loginData: LoginData;

  constructor(private timesheetService: TimesheetService, private formBuilder: FormBuilder, private router:Router) {
    this.inputForm = this.formBuilder.group({
      loginUserName: '',
      password: ''
    });
  }

  ngOnInit(): void {
    this.date.setMonth(this.date.getMonth()-1);
    this.monthName= this.displayMonth();
    this.getStudents(this.date.getFullYear(), this.date.getMonth()+1);
    this.loginData=this.timesheetService.loginData;
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
    this.timesheetService.getStudents(year, month)
      .subscribe(res=> {this.timesheets= res;
        }, error => {
          if (error.status==0) alert("Connection refused");
          });
  }

  createExcel(timesheet: Timesheet){
    if (sessionStorage.getItem('loginUserName')==null||sessionStorage.getItem('password')==null){
      this.router.navigate(['/login']);
    } else{
      this.timesheetService.createTimesheet(timesheet.student.userName, timesheet.year, timesheet.month)
        .subscribe(responseData => {
          timesheet.fileExists= responseData.fileExists;
          console.log("Created File");
        }, error => {
          if (error.status==0)alert("Connection refused");
          else if (error.status==500) alert("500: Internal Servererror. Es könnte ein Problem mit der Proxy Konfiguration sein.")
          else alert(error.error);
        });
      }

  }

  openExcel(timesheet: Timesheet) {
    this.timesheetService.openTimesheet(timesheet.student.userName, timesheet.year, timesheet.month)
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
         else alert(error.error);
        console.log('download error');
      }, () => {
        console.log('Completed file download.')
      });
  }

  showDialog(timesheet: Timesheet){
    this.dialog=true;
    this.selectedTimesheet= timesheet;
  }

  async deleteStudent(){
    await this.timesheetService.deleteStudent(this.selectedTimesheet.student).toPromise()
      .then(res => {
        console.log("res: "+ res);
      })
      .catch(err => { alert(err.error)});
      this.getStudents(this.date.getFullYear(), this.date.getMonth()+1);
      this.dialog= false;
  }
  async deleteTimesheet(){
    await this.timesheetService.deleteTimesheet(this.selectedTimesheet).toPromise()
      .then(res => {
        console.log("res: "+ res);
      })
      .catch(err => { alert(err.error)});
      this.getStudents(this.date.getFullYear(), this.date.getMonth()+1);
      this.dialog= false;
  }

  cancel(){
    this.dialog=false;
    this.selectedTimesheet= null;
  }

  onSubmit(data){
    this.timesheetService.loginData= data;
    this.loginData=data;
    this.inputForm.reset();
  //  document.getElementById("loginInfo").innerText= "Sie sind angemeldet als "+data.loginUserName;
  }

 /*   async onDelete(timesheet: Timesheet){
      if(confirm("Delete Student "+timesheet.student.firstName+ " "+timesheet.student.lastName +" and all corresponding files?")){
          await this.timesheetService.deleteTimesheet(timesheet).toPromise()
            .then(res => {
              console.log("res: "+ res);
            })
            .catch(err => { alert(err.body)});
      } else{
        if(confirm("Only delete Student?")){
          await this.timesheetService.deleteStudent(timesheet.student).toPromise()
            .then(res => {
              console.log("res:"+ res);
            })
            .catch(err => { alert(err.body)});
        } else console.log("Cancel");
      }
      this.getStudents(this.date.getFullYear(), this.date.getMonth()+1);
    }*/

  displayMonth(): string {
    let monthNames= ["Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August","September", "Oktober", "November", "Dezember"];
    return monthNames[this.date.getMonth()]+" "+ this.date.getFullYear();
  }



}
