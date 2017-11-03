import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { NgRedux, select } from '@angular-redux/store';
import { CounterActions } from './app.actions';
import {IAppState} from '../store';
import { Observable } from 'rxjs/Observable';

import 'rxjs/Rx' ;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  @select() readonly count$: Observable<number>;

  constructor(
    private ngRedux: NgRedux<IAppState>,
    private actions: CounterActions) {}
}


