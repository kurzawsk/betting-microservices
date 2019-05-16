import {Injectable} from '@angular/core';
import {PagedOuterDataSource} from '../../services/paged-outer-data-source.service';
import {Job, PagedSearchResult} from '../common/model/data-model';
import {Observable} from 'rxjs/index';
import {JobService} from './job.service';

@Injectable()
export class JobDataSource extends PagedOuterDataSource<Job> {

  constructor(protected jobService: JobService) {
    super();
  }

  protected getItems(): Observable<PagedSearchResult> {
    return this.jobService.getJobs(this.pagedSearchParams);
  }

}
