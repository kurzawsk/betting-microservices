import {Injectable} from '@angular/core';
import {PagedOuterDataSource} from '../../services/paged-outer-data-source.service';
import {PagedSearchResult} from '../common/model/data-model';
import {Observable} from 'rxjs/index';
import {MatchService} from './match.service';
import {Team} from './model/data-model';

@Injectable()
export class TeamDataSource extends PagedOuterDataSource<Team> {

  constructor(protected matchService: MatchService) {
    super();
  }

  protected getItems(): Observable<PagedSearchResult> {
    return this.matchService.getTeams(this.pagedSearchParams);
  }

}
