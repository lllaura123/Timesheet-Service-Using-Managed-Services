import { Injectable,EventEmitter, Component, Output } from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
    message: string= null;
    alertMessage: string=null;
    loginDataExists: BehaviorSubject<boolean>;
  constructor() { this.loginDataExists= new BehaviorSubject<boolean>(false); }

  observeLoginData(){
    return this.loginDataExists.asObservable();
  }
  public setLoginDataExists(newValue: boolean): void {
      this.loginDataExists.next(newValue);
  }

  @Output() public executeRequest: EventEmitter<any> = new EventEmitter();
}
