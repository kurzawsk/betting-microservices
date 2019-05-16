import {Injectable} from '@angular/core';
import {PagedOuterDataSource} from '../../services/paged-outer-data-source.service';
import {PagedSearchResult} from '../common/model/data-model';
import {Observable} from 'rxjs/index';
import {ReportingService} from './reporting.service';
import {Report} from './model/data-model';

@Injectable()
export class ReportingDataSource extends PagedOuterDataSource<Report> {

  constructor(protected reportingService: ReportingService) {
    super();
  }

  protected getItems(): Observable<PagedSearchResult> {
    return this.reportingService.getReports(this.pagedSearchParams);
  }

}
