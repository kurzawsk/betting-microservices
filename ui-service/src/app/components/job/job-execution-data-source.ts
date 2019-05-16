import {Injectable} from '@angular/core';
import {PagedOuterDataSource} from '../../services/paged-outer-data-source.service';
import {JobExecution, PagedSearchResult} from '../common/model/data-model';
import {Observable} from 'rxjs/index';
import {EMPTY} from 'rxjs';
import {JobService} from './job.service';

@Injectable()
export class JobExecutionDataSource extends PagedOuterDataSource<JobExecution> {

  constructor(protected jobService: JobService) {
    super();
  }

  protected getItems(): Observable<PagedSearchResult> {
    if (this.pagedSearchParams.filter.jobId) {
      return this.jobService.getJobExecutions(this.pagedSearchParams);
    } else {
      return EMPTY;
    }
  }

}
