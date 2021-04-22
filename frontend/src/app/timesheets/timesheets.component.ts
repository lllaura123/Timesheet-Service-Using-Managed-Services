import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Timesheet } from '../timesheets';
import {LoginData} from '../loginData';
import { TimesheetService } from '../timesheet.service';
import { MessageService } from '../message.service';

import { HttpHeaders} from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import '@angular/localize/init'
import Amplify, { Auth } from 'aws-amplify';
//import AmplifyService from "../../node_modules/aws-amplify/lib-esm"
import { AmplifyService } from 'aws-amplify-angular';
//import {SESSION_STORAGE, WebStorageService} from 'angular-webstorage-service';
import * as AWS from 'aws-sdk';

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
  group: string;


  constructor(private timesheetService: TimesheetService, private formBuilder: FormBuilder, private router:Router, private messageService: MessageService, private amplifyService:AmplifyService) {
    this.inputForm = this.formBuilder.group({
      loginUserName: '',
      password: ''
    });

    this.observeAuthStateChange(amplifyService);
  }

  ngOnInit(): void {
    this.date.setMonth(this.date.getMonth()-1);
    this.monthName= this.displayMonth();
    this.getStudents(this.date.getFullYear(), this.date.getMonth()+1);
    this.loginData=this.timesheetService.loginData;
    this.group= sessionStorage.getItem("group");
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
      .subscribe(res=> {
          this.timesheets= res;
        }, error => {
          if (error.status==0) this.messageService.alertMessage=$localize`:@@connectionRefused:Verbindung wurde abgelehnt`;
          });

  }

  createExcel(timesheet: Timesheet){
    if (sessionStorage.getItem('loginUserName')==null||sessionStorage.getItem('password')==null){
   /*   this.messageService.somethingChanged.subscribe((data: any) => {
          console.log("Data from login component", data);
      })*/
      this.messageService.observeLoginData().subscribe(exists=>{
        if (exists){
          this.timesheetService.createTimesheet(timesheet.student.userName, timesheet.year, timesheet.month)
            .subscribe(responseData => {
              timesheet.fileExists= responseData.fileExists;
              console.log("Created File");
              //window.location.reload();
            }, error => {
              if (error.status==0)this.messageService.alertMessage=$localize`:@@connectionRefused:Verbindung wurde abgelehnt`;
              else if (error.status==500) this.messageService.alertMessage=$localize`:@@serverError:500: Internal Servererror. Es könnte ein Problem mit der Proxy Konfiguration sein.`;
              else this.messageService.alertMessage=error.error;
            });
        }});
      this.router.navigate(['/login']);
    } else{
      this.timesheetService.createTimesheet(timesheet.student.userName, timesheet.year, timesheet.month)
        .subscribe(responseData => {
          timesheet.fileExists= responseData.fileExists;
          console.log("Created File");
        }, error => {
          if (error.status==0)this.messageService.alertMessage=$localize`:@@connectionRefused:Verbindung wurde abgelehnt`;
          else if (error.status==500) this.messageService.alertMessage=$localize`:@@serverError:500: Internal Servererror. Es könnte ein Problem mit der Proxy Konfiguration sein.`;
          else this.messageService.alertMessage=error.error;
        });
      }

  }

  openExcel(timesheet: Timesheet) {
    this.timesheetService.openTimesheet(timesheet.student.userName, timesheet.year, timesheet.month)
     .subscribe(res => {
        var headers: HttpHeaders= res.headers;
        var filename= headers.get('fileName');
        console.log('start download for '+filename+':',res);
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
         if (error.status==0) this.messageService.alertMessage=$localize`:@@connectionRefused:Verbindung wurde abgelehnt`;
         else this.messageService.alertMessage= error.error;
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
      .then(res => { console.log(res);
      this.deleteUserInCognito(this.selectedTimesheet.student.userName);
      })
      .catch(err => this.messageService.alertMessage=err.error);
      this.getStudents(this.date.getFullYear(), this.date.getMonth()+1);
      this.dialog= false;
  }
  async deleteTimesheet(){
    await this.timesheetService.deleteTimesheet(this.selectedTimesheet).toPromise()
      .then(res => {
        console.log("res: "+ res);
        this.deleteUserInCognito(this.selectedTimesheet.student.userName);
      })
      .catch(err => this.messageService.alertMessage= err.error);
      this.getStudents(this.date.getFullYear(), this.date.getMonth()+1);
      this.dialog= false;
  }

  cancel(){
    this.dialog=false;
    this.selectedTimesheet= null;
  }



  displayMonth(): string {;
    let monthNames= [$localize`:@@january:Januar`, $localize`:@@february:Februar`, $localize`:@@march:März`, $localize`:@@april:April`, $localize`:@@may:Mai`, $localize`:@@june:Juni`,
                    $localize`:@@july:Juli`, $localize`:@@august:August`,$localize`:@@september:September`, $localize`:@@october:Oktober`, $localize`:@@november:November`, $localize`:@@december:Dezember`];

    return monthNames[this.date.getMonth()]+" "+ this.date.getFullYear();
  }

  observeAuthStateChange(amplifyService: AmplifyService){
    this.amplifyService = amplifyService;
    this.amplifyService.authStateChange$
      .subscribe(authState => {
        if (authState.state === 'signedIn') {
          console.log("User logged in");
          Auth.currentSession()
           .then(data => {
              this.group= data.getIdToken().payload['cognito:groups'][0];
              sessionStorage.setItem("group", this.group);
              if (this.group!="manager"){
                (<HTMLInputElement> document.getElementById("routerLink")).disabled = true;
                } else{
                (<HTMLInputElement> document.getElementById("routerLink")).disabled = false;
              }

              })
           .catch(err => console.log(err));
        }

      });
  }

    deleteUserInCognito(userName: string){
      var params = {
        UserPoolId: 'eu-central-1_8v7hXcYFi', /* required */
        Username: userName /* required */
      };

      this.getCognitoIdentityServiceProvider().then(cognitoidentityserviceprovider=>{
        cognitoidentityserviceprovider.adminDeleteUser(params, function(err, data) {
          if (err) console.log(err, err.stack); // an error occurred
          else     console.log(data);           // successful response
        });
      });
    }

    getCognitoIdentityServiceProvider(){
      var AWS = require('aws-sdk');
      var cognitoIdentityServiceProvider= this.getAWSCredentials().then(userCredentials=>{
          AWS.config.update({credentials: userCredentials ,region: 'eu-central-1'});
          var cognitoidentityserviceprovider = new AWS.CognitoIdentityServiceProvider();
          return cognitoidentityserviceprovider;
      });
      return cognitoIdentityServiceProvider;
    }

    getAWSCredentials(){
      var awsCredentials= this.getToken()
        .then(value=>{
           var myCredentials = new AWS.CognitoIdentityCredentials({
              IdentityPoolId: 'eu-central-1:50b3eb91-f29d-4cdf-88af-e57d991a4670',
              Logins: {
                'cognito-idp.eu-central-1.amazonaws.com/eu-central-1_8v7hXcYFi': value
              }
            });
            console.log(myCredentials);
            return myCredentials;
        });
      return awsCredentials;

    }

    getToken(): Promise<string>{
      var token= Auth.currentSession()
       .then(data => {
          return data.getIdToken().getJwtToken();
        });
        return token;
    }



}
