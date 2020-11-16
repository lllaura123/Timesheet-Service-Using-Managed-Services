import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import {LoginData} from '../loginData';
import { TimesheetService } from '../timesheet.service';
import { LoginService } from '../login.service';
import { MessageService } from '../message.service';
import { FormBuilder } from '@angular/forms';
//import {SESSION_STORAGE, WebStorageService} from 'angular-webstorage-service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  inputForm;
  loginData: LoginData;
//  inputShown: boolean= true;
  errorMessage: string= null;

  constructor(private loginService: LoginService, private formBuilder: FormBuilder, private router: Router, private location: Location, private messageService: MessageService) {
    this.inputForm = this.formBuilder.group({
      loginUserName: '',
      password: ''
    });
  }
    ngOnInit(): void {
    }
  submitLoginData(data){
    this.loginService.validateLogin(data).subscribe(res=> {
      console.log("res: "+res);
      sessionStorage.setItem('loginUserName', data.loginUserName);
      sessionStorage.setItem('password', data.password);
      this.errorMessage= null;
      this.messageService.message= $localize`:@@loginSuccess:Login war erfolgreich`;
      this.messageService.setLoginDataExists(true);
    /*  this.messageService.executeRequest.emit({
          dataExists: true;
      });*/
 //     setTimeout((document.getElementById("success").style.display= "block"),5000);
      this.location.back();

      /*this.loginData=data;
      this.inputForm.reset();
      document.getElementById("loginInput").style.display="none";
      this.inputShown= false;*/
     // this.router.navigate(['/timesheets']);
      },err=> {
        if (err.status==500) {this.errorMessage= $localize`:@@serverError:500: Internal Servererror. Es könnte ein Problem mit der Proxy Konfiguration sein.`; this.messageService.alertMessage=this.errorMessage}
        else if (err.status>=400) {this.messageService.alertMessage= err.error;}
        this.inputForm.reset();
      });

  //  document.getElementById("loginInfo").innerText= "Sie sind angemeldet als "+data.loginUserName;
  }

    cancel(){
      this.location.back();
    }

    closeAlert() {
      document.getElementById("alert").style.display= "none";
    }
    closeMessage(){
      document.getElementById("success").style.display="none";
    }
}
