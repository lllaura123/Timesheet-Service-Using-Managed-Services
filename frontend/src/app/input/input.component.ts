import { Component, OnInit } from '@angular/core';
import { TimesheetService } from '../timesheet.service';
import { MessageService } from '../message.service';
import {LoginData} from '../loginData';

import { HttpHeaders} from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import Amplify, { Auth } from 'aws-amplify';
import * as AWS from 'aws-sdk';


@Component({
  selector: 'app-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.css']
})
export class InputComponent implements OnInit {
  inputForm;
  loginData: LoginData;
  errorMessage: string= null;

  constructor(private timesheetService: TimesheetService, private formBuilder: FormBuilder, private router: Router, private messageService: MessageService) {
    this.inputForm = this.formBuilder.group({
      firstName: '',
      lastName: '',
      userName: '',
      studentEmail: ''
    });
    this.loginData= this.timesheetService.loginData;
  }

  ngOnInit(): void {
  }

  async submitStudentData(studentData){
    if(sessionStorage.getItem("group")=="manager"){
      if (sessionStorage.getItem('loginUserName')==null||sessionStorage.getItem('password')==null){
        this.messageService.observeLoginData().subscribe(exists=>{
        if(exists){
            this.timesheetService.postNewStudent(studentData)
            .subscribe(res=> {this.messageService.message= res;
            this.createUserInCognito(studentData.userName, studentData.studentEmail);
            this.router.navigate(['/timesheets']);
            },err=>{
              if (err.status==0) this.messageService.alertMessage=$localize`:@@connectionRefused:Verbindung wurde abgelehnt`;
              else if(err.status==500) this.messageService.alertMessage=$localize`:@@serverError:500: Internal Servererror. Es könnte ein Problem mit der Proxy Konfiguration sein.`;
              else if(err.status>=400) this.messageService.alertMessage=err.error;
              this.inputForm.reset();
            });
        }});
        this.router.navigate(['/login']);
      } else{
          await this.timesheetService.postNewStudent(studentData)
            .subscribe(res=> {this.messageService.message= res;
            this.createUserInCognito(studentData.userName, studentData.studentEmail);
            this.router.navigate(['/timesheets']);
            },err=>{
              if (err.status==0) this.messageService.alertMessage=$localize`:@@connectionRefused:Verbindung wurde abgelehnt`;
              else if(err.status==500) this.messageService.alertMessage=$localize`:@@serverError:500: Internal Servererror. Es könnte ein Problem mit der Proxy Konfiguration sein.`;
              else if(err.status>=400) this.messageService.alertMessage=err.error;
          });
        this.errorMessage==null;
        this.inputForm.reset();
      }
   } else {
    this.messageService.alertMessage="Studenten dürfen keine User hinzufügen";
    }
    //this.createUserInCognito(studentData.userName);
  }

  cancel(){
    this.router.navigate(['/timesheets']);
  }

  close(){
    document.getElementById("alert").style.display= "none";
  }

  async createUserInCognito(userName:string, studentEmail:string){
    console.log("checkpoint");
    var params = {
      UserPoolId: 'eu-central-1_8v7hXcYFi', /* required */
      Username: userName, /* required */
      DesiredDeliveryMediums: [
        "EMAIL"
        /* more items */
      ],
      ForceAliasCreation: false,
      //MessageAction: "RESEND",
      TemporaryPassword: 'Password2.',
      UserAttributes: [
        {
          Name: 'email', /* required */
          Value: studentEmail
        },
        /*{
          Name: 'email_verified',
          Value: 'False'
        }/*
        /* more items */
      ]
    };
    this.getCognitoIdentityServiceProvider().then(cognitoidentityserviceprovider=>{
      cognitoidentityserviceprovider.adminCreateUser(params, function(err, data) {
        if (err) console.log(err, err.stack); // an error occurred
        else     console.log(data);           // successful response
      });
    });
  }

  /*addCognitoUserToGroup(userName: string){
    var params = {
      GroupName: 'students',
      UserPoolId: 'eu-central-1_8v7hXcYFi',
      Username: userName
    };
    this.getCognitoIdentityServiceProvider().then(cognitoidentityserviceprovider=>{
      cognitoidentityserviceprovider.adminAddUserToGroup(params, function(err, data) {
        if (err) console.log(err, err.stack); // an error occurred
        else     console.log(data);           // successful response
      });
    });
  }*/

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
