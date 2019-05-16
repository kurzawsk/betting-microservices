import {Injectable} from '@angular/core';
import {EMPTY, Observable} from 'rxjs/index';
import {MatchService} from './match.service';
import {PagedSearchResult} from '../common/model/data-model';
import {PagedOuterDataSource} from '../../services/paged-outer-data-source.service';
import {MatchOdd} from './model/data-model';

@Injectable()
export class MatchOddDataSource extends PagedOuterDataSource<MatchOdd> {


  constructor(protected matchService: MatchService) {
    super();
  }

  protected getItems(): Observable<PagedSearchResult> {
    if (this.pagedSearchParams.filter.matchId) {
      return this.matchService.getMatchOdds(this.pagedSearchParams);
    } else {
      return EMPTY;
    }
  }

}
