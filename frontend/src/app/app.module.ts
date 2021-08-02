import { BrowserModule } from '@angular/platform-browser';
import { NgModule, LOCALE_ID } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
//import { StorageServiceModule} from 'angular-webstorage-service';


import { AppComponent } from './app.component';
import { TimesheetsComponent } from './timesheets/timesheets.component';
import { InputComponent } from './input/input.component';
import { AppRoutingModule } from './app-routing.module';
import { LoginComponent } from './login/login.component';

import { AmplifyUIAngularModule } from '@aws-amplify/ui-angular';
import Amplify, { Auth } from 'aws-amplify';
import { AmplifyService} from 'aws-amplify-angular';

Amplify.configure({
    Auth: {

        // OPTIONAL - Amazon Cognito User Pool ID
        userPoolId: 'eu-central-1_8v7hXcYFi',
        // OPTIONAL - Amazon Cognito Web Client ID (26-char alphanumeric string)
        userPoolWebClientId: '217klo8d9echpdt5c99kp2qs8i',

        oauth: {
            domain: 'https://timesheet-approve-service.auth.eu-central-1.amazoncognito.co',
            scope: ['email', 'openid'],
            //redirectSignIn: 'https://3.126.32.225:8080',
            //redirectSignOut: 'https://3.126.32.225:8080',
            redirectSignIn: 'https://ec2-3-66-184-101.eu-central-1.compute.amazonaws.com:8080/timesheets/',
            redirectSignOut: 'https://ec2-3-66-184-101.eu-central-1.compute.amazonaws.com:8080/timesheets/',
            responseType: 'code' // or 'token', note that REFRESH token will only be generated when the responseType is code
        }
    }
});
@NgModule({
  declarations: [
    AppComponent,
    TimesheetsComponent,
    InputComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    HttpClientInMemoryWebApiModule,
    AppRoutingModule,
//    StorageServiceModule
    AmplifyUIAngularModule,
   // AmplifyAngularModule
  ],
  providers:  [AmplifyService],
  bootstrap: [AppComponent]
})
export class AppModule { }
