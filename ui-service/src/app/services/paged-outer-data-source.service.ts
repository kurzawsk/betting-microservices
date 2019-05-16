import {Injectable} from '@angular/core';
import {map} from 'rxjs/operators';
import {DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, Observable} from 'rxjs/index';
import {OuterDataSource} from '../components/common/ui-grid/interfaces/OuterDataSource';
import {PagedSearchParams, PagedSearchResult} from '../components/common/model/data-model';


@Injectable()
export abstract class PagedOuterDataSource<T> extends DataSource<T> implements OuterDataSource {

  public static readonly ENTRY_SORT_PROPERTY = 'id';
  public static readonly PAGE_SIZE = 10;

  public loadingFinishedSubj: BehaviorSubject<boolean> = new BehaviorSubject(true);
  public pagedSearchParams = new PagedSearchParams();
  public pagedSearchResult = new BehaviorSubject<PagedSearchResult>(new PagedSearchResult());
  public selectedRowId: BehaviorSubject<number> = new BehaviorSubject(null);
  private _prepareData: (rawData: T[]) => T[];

  constructor() {
    super();
  }

  public get prepareData() {
    return this._prepareData;
  }

  public set prepareData(_prepareData: (rawData: T[]) => T[]) {
    this._prepareData = _prepareData;
  }

  public connect(): Observable<T[]> {
    return this.pagedSearchResult.pipe(map(r => r.items));
  }

  public disconnect(): void {
  }

  public refresh({paginator = null, sort = null, filter = null}) {
    if (paginator && sort) {
      this.pagedSearchParams.pageIndex = paginator.pageIndex;
      this.pagedSearchParams.pageSize = paginator.pageSize;
      this.pagedSearchParams.sortOrderAsc = sort.direction !== '' ? (sort.direction.toUpperCase() !== 'DESC') : true;
      this.pagedSearchParams.sortProperty = sort.active !== undefined ? sort.active : this.pagedSearchParams.sortProperty;
    }
    if (filter) {
      this.pagedSearchParams.filter = filter;
    }

    return this.loadItems();
  }

  public refreshDo() {
    this.refresh({}).subscribe(
      res => this.onResult(res),
      err => this.onError(err)
    );
  }

  public onResult(res) {
    this.loadingFinishedSubj.next(true);
    this.pagedSearchResult.next({items: this.prepareData(res.items), totalItemsCount: res.totalItemsCount});
  }

  public onError(err) {
    alert('Error occurred: ' + JSON.stringify(err.message));
    this.loadingFinishedSubj.next(true);
  }

  public reset = () => {
    this.pagedSearchParams = {
      pageIndex: 0,
      pageSize: PagedOuterDataSource.PAGE_SIZE,
      sortProperty: PagedOuterDataSource.ENTRY_SORT_PROPERTY,
      sortOrderAsc: true,
      filter: {}
    };
    this.selectedRowId.next(null);
    return this.loadItems();
  }

  protected loadItems = (): Observable<any> => {
    this.loadingFinishedSubj.next(false);
    return this.getItems();
  }

  protected abstract getItems(): Observable<PagedSearchResult>;



}
