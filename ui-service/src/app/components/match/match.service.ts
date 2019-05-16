import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs/index';
import {EnvHostService} from '../../services/env-host.service';
import {PagedSearchParams, PagedSearchResult} from '../common/model/data-model';
import {Utils} from '../../utils/Utils';
import {Match, Team} from './model/data-model';

@Injectable()
export class MatchService {

  private MATCH_URL = this.envHostService.getGatewayUrl() + 'mdm-service/match';
  private TEAM_URL = this.envHostService.getGatewayUrl() + 'mdm-service/team';

  public getMatches = (pagedSearchParams: PagedSearchParams): Observable<PagedSearchResult> => {
    let params: HttpParams = Utils.toHttpParams(pagedSearchParams);
    if (pagedSearchParams.filter) {
      params = params.append('teamsName', pagedSearchParams.filter.teamsName || '');
      params = params.append('resultTypes', pagedSearchParams.filter.resultTypes || '');
    }
    return this.http.get<PagedSearchResult>(this.MATCH_URL, {params, responseType: 'json'});
  }
  public getMatchesForTeam = (pagedSearchParams: PagedSearchParams): Observable<PagedSearchResult> => {
    let params: HttpParams = Utils.toHttpParams(pagedSearchParams);
    if (pagedSearchParams.filter) {
      params = params.append('teamId', pagedSearchParams.filter.teamId || '');
    }
    return this.http.get<PagedSearchResult>(this.MATCH_URL, {params, responseType: 'json'});
  }
  public getMatch = (id: number): Observable<Match> => {
    const params = new HttpParams();
    return this.http.get<Match>(this.MATCH_URL + '/' + id, {params, responseType: 'json'});
  }
  public getMatchOdds = (pagedSearchParams: PagedSearchParams): Observable<PagedSearchResult> => {
    const params: HttpParams = Utils.toHttpParams(pagedSearchParams);
    return this.http.get<PagedSearchResult>(this.MATCH_URL + '/' + pagedSearchParams.filter.matchId + '/odd',
      {params, responseType: 'json'});
  }
  public getTeams = (pagedSearchParams: PagedSearchParams): Observable<PagedSearchResult> => {
    let params: HttpParams = Utils.toHttpParams(pagedSearchParams);
    if (pagedSearchParams.filter) {
      params = params.append('name', pagedSearchParams.filter.name || '');
    }
    params = params.append('basic-info-only', 'false');
    return this.http.get<PagedSearchResult>(this.TEAM_URL, {params, responseType: 'json'});
  }
  public getTeam = (id: number): Observable<Team> => {
    let params = new HttpParams();
    params = params.append('basic-info-only', 'false');
    return this.http.get<Team>(this.TEAM_URL + '/' + id, {params, responseType: 'json'});
  }


  constructor(private http: HttpClient, private envHostService: EnvHostService) {
  }
}
