import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import {LoginData} from './loginData';
import { TimesheetService } from './timesheet.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
/*  title = 'Timesheets';
  inputForm;
  loginData: LoginData;
  inputShown: boolean= true;

  constructor(private timesheetService: TimesheetService, private formBuilder: FormBuilder) {
    this.inputForm = this.formBuilder.group({
      loginUserName: '',
      password: ''
    });
  }
  onSubmit(data){

    this.timesheetService.validateLogin(data).subscribe(res=> {
      console.log("res: "+res);
      this.timesheetService.loginData= data;
      this.loginData=data;
      this.inputForm.reset();
      document.getElementById("loginInput").style.display="none";
      this.inputShown= false;

      },err=> {
        if (err.status>=400)alert(err.error);
        this.inputForm.reset();
      });
  //  document.getElementById("loginInfo").innerText= "Sie sind angemeldet als "+data.loginUserName;
  }
  closeLoginInput(){
    document.getElementById("loginInput").style.display="none";
    this.inputShown=false;
  }
  openLoginInput(){
    document.getElementById("loginInput").style.display="block";
    this.inputShown= true;
  }*/
}
