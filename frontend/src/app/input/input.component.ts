import { Component, OnInit } from '@angular/core';
import { TimesheetService } from '../timesheet.service';
import { MessageService } from '../message.service';
import {LoginData} from '../loginData';

import { HttpHeaders} from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';

//import {SESSION_STORAGE, WebStorageService} from 'angular-webstorage-service';

@Component({
  selector: 'app-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.css']
})
export class InputComponent implements OnInit {
  inputForm;
  loginData: LoginData;
  errorMessage: string= null;
  //showLoginInput: boolean;

  constructor(private timesheetService: TimesheetService, private formBuilder: FormBuilder, private router: Router, private messageService: MessageService) {
    this.inputForm = this.formBuilder.group({
      firstName: '',
      lastName: '',
      userName: ''
    });
    this.loginData= this.timesheetService.loginData;
  }

  ngOnInit(): void {
    //document.getElementById("alert").style.display= "none";
  }

  async submitStudentData(studentData){
    if (sessionStorage.getItem('loginUserName')==null||sessionStorage.getItem('password')==null){
      this.messageService.observeLoginData().subscribe(exists=>{
      if(exists){
          this.timesheetService.postNewStudent(studentData)
          .subscribe(res=> {this.messageService.message= res;
          this.router.navigate(['/timesheets']);
          },err=>{
            if (err.status==0) this.messageService.alertMessage="Verbindung zum Backend wurde abgelehnt";
            else if(err.status==500) this.messageService.alertMessage="Internal Server Error. Es könnte ein Problem mit der Proxy Konfiguration geben.";
            else if(err.status>=400) this.messageService.alertMessage=err.error;
            this.inputForm.reset();
          });
      }});
      this.router.navigate(['/login']);
    } else{
 //     try{
        await this.timesheetService.postNewStudent(studentData)
          .subscribe(res=> {this.messageService.message= res;
          this.router.navigate(['/timesheet']);
          },err=>{
            if (err.status==0) this.messageService.alertMessage="Verbindung zum Backend wurde abgelehnt";
            else if(err.status==500) this.messageService.alertMessage="Internal Server Error. Es könnte ein Problem mit der Proxy Konfiguration geben.";
            else if(err.status>=400) this.messageService.alertMessage=err.error;
        });
   //     }catch(err){
     //     console.log(err.error)};
      this.errorMessage==null;
      this.inputForm.reset();
    }
  }

  cancel(){
    this.router.navigate(['/timesheets']);
  }

  close(){
    document.getElementById("alert").style.display= "none";
  }

}
