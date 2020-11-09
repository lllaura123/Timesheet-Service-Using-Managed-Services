import { Component, OnInit } from '@angular/core';
import { TimesheetService } from '../timesheet.service';
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
  showErrorModal: boolean=false;
  //showLoginInput: boolean;

  constructor(private timesheetService: TimesheetService, private formBuilder: FormBuilder, private router: Router) {
    this.inputForm = this.formBuilder.group({
      firstName: '',
      lastName: '',
      userName: ''
    });
    this.loginData= this.timesheetService.loginData;
  }

  ngOnInit(): void {
  }

  async submitStudentData(studentData){
    if (sessionStorage.getItem('loginUserName')==null||sessionStorage.getItem('password')==null){
      this.router.navigate(['/login']);
    } else{
      await this.timesheetService.postNewStudent(studentData).toPromise()
        .catch(err=> {
           if (err.status==0) alert("Connection refused");
           else if(err.status==500)alert("500: Internal Server error. Es könnte ein Problem mit der Proxy Konfiguration sein.")
           else if (err.status>=400) { alert(err.error);}
           });
      this.inputForm.reset();
      this.router.navigate(['/timesheets']);
    }
  }

  cancel(){
    this.router.navigate(['/timesheets']);
  }


}
