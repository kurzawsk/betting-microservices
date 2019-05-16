import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {BehaviorSubject, Subscription} from 'rxjs/index';

@Component({
  selector: 'app-spinner-container',
  templateUrl: './spinner-container.component.html',
  styleUrls: ['./spinner-container.component.scss']
})
export class SpinnerContainerComponent implements OnInit, OnDestroy {

  display = false;

  @Input()
  diameter: number;

  @Input()
  loadingFinishedSubj: BehaviorSubject<boolean> = new BehaviorSubject(true);

  private subscription: Subscription;

  constructor() {
  }

  @Input()
  public active(isActive: boolean) {
    this.loadingFinishedSubj.next(isActive);
  }

  ngOnInit() {
    if (this.loadingFinishedSubj) {
      this.subscription = this.loadingFinishedSubj.subscribe(loadingFinished => {
        setTimeout(_ => this.display = !loadingFinished);
      });
    } else {
      setTimeout(_ => this.display = true);
    }
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}


