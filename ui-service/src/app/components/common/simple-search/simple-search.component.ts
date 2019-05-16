import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormControl} from '@angular/forms';
import {debounceTime, distinctUntilChanged} from 'rxjs/operators';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-simple-search',
  templateUrl: './simple-search.component.html',
  styleUrls: ['./simple-search.component.scss']
})
export class SimpleSearchComponent implements OnInit, OnDestroy {
  private static readonly DELAY = 150;
  userInputControl = new FormControl();
  @Input()
  placeholder?: string;
  @Output()
  eventEmitter: EventEmitter<string> = new EventEmitter();
  private subscription: Subscription;

  constructor() {
  }

  ngOnInit() {

    // this.userInputControl.valueChanges.pipe(
    //   debounceTime(SimpleSearchComponent.DELAY),
    //   distinctUntilChanged(),
    //   switchMap(term => this.smartSearchService.createSearchRequest(term))
    // ).subscribe(res => this.smartSearchService.onSearchFinished(res, this.userInputControl.value));

    this.subscription = this.userInputControl.valueChanges.pipe(
      debounceTime(SimpleSearchComponent.DELAY),
      distinctUntilChanged()).subscribe(s => this.eventEmitter.emit(s));
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }


}
