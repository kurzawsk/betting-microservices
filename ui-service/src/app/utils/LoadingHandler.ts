import {of as observableOf, Subject, Subscription} from 'rxjs';
import {delay} from 'rxjs/operators';

export class LoadingHandler {
  private static readonly DELAY = 300;
  private subscription: Subscription;

  constructor(private loadingFinishedSubj: Subject<any>) {
    this.subscription = observableOf(null).pipe(delay(LoadingHandler.DELAY)).subscribe(() => {
      loadingFinishedSubj.next(false);
    });
  }

  public onLoadingFinished() {
    this.subscription.unsubscribe();
    this.loadingFinishedSubj.next(true);
  }
}
