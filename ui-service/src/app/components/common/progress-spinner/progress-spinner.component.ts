import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subject, Subscription} from 'rxjs';

@Component({
  selector: 'app-progress-spinner',
  templateUrl: './progress-spinner.component.html',
  styleUrls: ['./progress-spinner.component.scss']
})
export class SpinnerComponent implements OnInit, OnDestroy {
  private static readonly DEFAULT_COLOR = 'info';
  private static readonly DEFAULT_MODE = 'indeterminate';
  private static readonly DEFAULT_STROKE_WIDTH = 3;
  private static readonly DEFAULT_DIAMETER = 100;

  _color = SpinnerComponent.DEFAULT_COLOR;
  _mode = SpinnerComponent.DEFAULT_MODE;
  _strokeWidth = SpinnerComponent.DEFAULT_STROKE_WIDTH;
  _diameter = SpinnerComponent.DEFAULT_DIAMETER;

  @Input()
  loadingFinishedSubj?: Subject<boolean>;

  @Input()
  diameter?: number;

  display = false;

  private subscription: Subscription;

  constructor() {
  }

  ngOnInit() {
    if (this.diameter) {
      this._diameter = this.diameter;
    }
    if (this.loadingFinishedSubj) {
      this.subscription = this.loadingFinishedSubj.subscribe(loadingFinished => {
        this.display = !loadingFinished;
      });
    } else {
      this.display = true;
    }
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

}
