import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/index';
import {JobOperation, PagedSearchParams, PagedSearchResult} from '../common/model/data-model';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Utils} from '../../utils/Utils';
import {EnvHostService} from '../../services/env-host.service';

@Injectable()
export class MappingCaseService {

  private MAPPING_CASE_URL = this.envHostService.getGatewayUrl() + 'mdm-service/mapping-case';

  constructor(private http: HttpClient, private envHostService: EnvHostService) { }


  public getMappingCases = (pagedSearchParams: PagedSearchParams): Observable<PagedSearchResult> => {
    let params: HttpParams = Utils.toHttpParams(pagedSearchParams);
    if (pagedSearchParams.filter) {
      params = params.append('status-filter', pagedSearchParams.filter.statuses || '');
    }
    return this.http.get<PagedSearchResult>(this.MAPPING_CASE_URL, {params, responseType: 'json'});
  }

  public accept = (id: number): Observable<any> => {
    const body = {accept: true};
    return this.http.post<any>(this.MAPPING_CASE_URL + '/' + id, body, {responseType: 'json'});
  }

  public reject = (id: number): Observable<any> => {
    const body = {accept: false};
    return this.http.post<any>(this.MAPPING_CASE_URL + '/' + id, body, {responseType: 'json'});
  }
}
