import {Injectable} from '@angular/core';
import {PagedSearchParams, PagedSearchResult} from '../common/model/data-model';
import {Observable} from 'rxjs/index';
import {Utils} from '../../utils/Utils';
import {HttpClient, HttpParams} from '@angular/common/http';
import {EnvHostService} from '../../services/env-host.service';
import {CustomReportParameters} from './model/data-model';

@Injectable()
export class ReportingService {

  private static readonly REPORTING_CUSTOM_PARAMETERS_PREFIX = 'report-';
  private REPORTING_URL = this.envHostService.getGatewayUrl() + 'reporting-service/report';

  constructor(private http: HttpClient, private envHostService: EnvHostService) {
  }

  public getReports = (pagedSearchParams: PagedSearchParams): Observable<PagedSearchResult> => {
    const params: HttpParams = Utils.toHttpParams(pagedSearchParams);
    return this.http.get<PagedSearchResult>(this.REPORTING_URL, {params, responseType: 'json'});
  }

  public executeReports = (ids: number[], customReportParameters: CustomReportParameters,
                           sendEmail: boolean, title: string = ''): Observable<any> => {
    let params;
    if (customReportParameters) {
      params = new HttpParams()
        .append(ReportingService.REPORTING_CUSTOM_PARAMETERS_PREFIX + 'from', this.getDateAsString(customReportParameters.fromDate))
        .append(ReportingService.REPORTING_CUSTOM_PARAMETERS_PREFIX + 'to', this.getDateAsString(customReportParameters.toDate))
        .append(ReportingService.REPORTING_CUSTOM_PARAMETERS_PREFIX + 'predefined-period', customReportParameters.predefinedPeriod || '');
    } else {
      params = new HttpParams();
    }

    params = params.append('send-email', '' + sendEmail);
    params = params.append('title', title);

    return this.http.post<any>(this.REPORTING_URL + '/' + ids.join(','), null, {params: params});
  }

  public toggleReportEnabled(id: number, enabled: boolean) {
    return this.http.patch<any>(this.REPORTING_URL + '/' + id, {statusEnabled: enabled});
  }


  private getDateAsString(date?: Date) {
    return date ? date.toISOString() : '';

  }
}
