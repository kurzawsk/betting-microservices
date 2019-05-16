import {Injectable} from '@angular/core';
import {PagedOuterDataSource} from '../../services/paged-outer-data-source.service';
import {PagedSearchResult} from '../common/model/data-model';
import {Observable} from 'rxjs/index';
import {MatchService} from './match.service';
import {Match} from './model/data-model';

@Injectable()
export class MatchDataSource extends PagedOuterDataSource<Match> {

  constructor(protected matchService: MatchService) {
    super();
  }

  protected getItems(): Observable<PagedSearchResult> {
    return this.matchService.getMatches(this.pagedSearchParams);
  }

}
