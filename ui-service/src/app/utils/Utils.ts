import {PagedSearchParams} from '../components/common/model/data-model';
import {HttpParams} from '@angular/common/http';

export class Utils {

  public static toHttpParams = (pagedSearchParams: PagedSearchParams): HttpParams => {
    return new HttpParams()
      .append('page', '' + pagedSearchParams.pageIndex)
      .append('size', '' + pagedSearchParams.pageSize)
      .append('sort-desc', '' + pagedSearchParams.sortOrderAsc)
      .append('sort-att', '' + pagedSearchParams.sortProperty);
  }
}
