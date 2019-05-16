import {Injectable} from '@angular/core';
import {PagedOuterDataSource} from '../../services/paged-outer-data-source.service';
import {MappingCase} from './model/data-model';
import {PagedSearchResult} from '../common/model/data-model';
import {Observable} from 'rxjs/index';
import {MappingCaseService} from './mapping-case.service';

@Injectable({
  providedIn: 'root'
})
export class MappingCaseDataSource extends PagedOuterDataSource<MappingCase> {
  protected getItems(): Observable<PagedSearchResult> {
    return this.mappingCaseService.getMappingCases(this.pagedSearchParams);
  }

  constructor(private mappingCaseService: MappingCaseService) {
    super();
  }
}
