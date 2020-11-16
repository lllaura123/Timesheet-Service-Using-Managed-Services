import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import {LoginData} from './loginData';
import { TimesheetService } from './timesheet.service';
import { MessageService } from './message.service';
import { LoginService } from './login.service';
import { LanguageService} from './language.service';

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
*/
  message: string;
  alertMessage: string;

  constructor(private timesheetService: TimesheetService, private messageService: MessageService, private languageService: LanguageService) {
  }

  getMessage():string{
    return this.messageService.message;
  }
  getAlertMessage(): string{

    return this.messageService.alertMessage;
  }
  closeMessage(){
    document.getElementById("success").style.display="none";
    this.messageService.message=null;
  }

  closeAlert() {
    document.getElementById("alert").style.display= "none";
    this.messageService.alertMessage=null;
  }
  openLink(link: string, lang:string){
    this.languageService.putLanguage(lang).subscribe(res=>{console.log(res); location.href=link});
    location.href=link;
  }


}
