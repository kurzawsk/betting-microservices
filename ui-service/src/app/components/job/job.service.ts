import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/index';
import {JobOperation, PagedSearchParams, PagedSearchResult} from '../common/model/data-model';
import {HttpClient, HttpParams} from '@angular/common/http';
import {EnvHostService} from '../../services/env-host.service';
import {Utils} from '../../utils/Utils';

@Injectable()
export class JobService {

  private JOB_URL = this.envHostService.getGatewayUrl() + 'job-service/job';

  constructor(private http: HttpClient, private envHostService: EnvHostService) {
  }

  public getJobs = (pagedSearchParams: PagedSearchParams): Observable<PagedSearchResult> => {
    const params: HttpParams = Utils.toHttpParams(pagedSearchParams);
    return this.http.get<PagedSearchResult>(this.JOB_URL, {params, responseType: 'json'});
  }
  public getJobExecutions = (pagedSearchParams: PagedSearchParams): Observable<PagedSearchResult> => {
    const params: HttpParams = this.toJobExecutionHttpParams(pagedSearchParams);
    return this.http.get<PagedSearchResult>(this.JOB_URL + '/' + pagedSearchParams.filter.jobId + '/execution',
      {params, responseType: 'json'});
  }

  public runJob = (id: number): Observable<any> => {
    const body = {operation: JobOperation.RUN_JOB};
    return this.http.post<any>(this.JOB_URL + '/' + id, body, {responseType: 'json'});
  }

  public toggleJob = (id: number, jobOperation: JobOperation): Observable<any> => {
    const body = {operation: jobOperation};
    return this.http.post<any>(this.JOB_URL + '/' + id, body, {responseType: 'json'});
  }

  private toJobExecutionHttpParams = (pagedSearchParams: PagedSearchParams): HttpParams => {
    return new HttpParams()
      .append('page', '' + pagedSearchParams.pageIndex)
      .append('size', '' + pagedSearchParams.pageSize)
      .append('sort-by-start-time-asc', '' + pagedSearchParams.sortOrderAsc)
      ;
  }


}
