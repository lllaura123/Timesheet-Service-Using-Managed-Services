import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import {LoginData} from './loginData';
import { TimesheetService } from './timesheet.service';
import { MessageService } from './message.service';
import { LoginService } from './login.service';
import { LanguageService} from './language.service';
import { HttpClient, HttpHeaders, HttpParams, HttpErrorResponse} from '@angular/common/http';

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
  URL="https://timesheet-approve-service.auth.eu-central-1.amazoncognito.com/login?response_type=code&client_id=217klo8d9echpdt5c99kp2qs8i&redirect_uri=http://localhost:4200/"
  message: string;
  alertMessage: string;

  constructor(private timesheetService: TimesheetService, private messageService: MessageService, private languageService: LanguageService, private http: HttpClient) {
  }
  /*ngOnInit(): void {
    /*if(sessionStorage.getItem('awsToken')==null){
      let params= new HttpParams();
      params.append("response-type", "code");
      params.append("client_id","217klo8d9echpdt5c99kp2qs8i");
      params.append("redirect_uri", "http://localhost:4200/")
      this.http.get("https://timesheet-approve-service.auth.eu-central-1.amazoncognito.com/oauth2/authorize",
      {params: params, observe: 'response'}).subscribe(response=>{
        console.log("Request success " +response.status);
        let awsToken= "abc"
        console.log(awsToken);
        sessionStorage.setItem("awsToken", awsToken);
      })
    }
  }*/

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

  }



}
