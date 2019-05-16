import {AfterViewInit, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {GridColumn, GridDefinitions} from './grid-definitions';
import {GridDataSource} from './grid-datasource';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material';
import {BehaviorSubject, merge} from 'rxjs';
import {switchMap} from 'rxjs/internal/operators/switchMap';
import {Subscription} from 'rxjs/index';

@Component({
  selector: 'app-grid',
  templateUrl: './grid.component.html',
  styleUrls: ['./grid.component.scss']
})
export class GridComponent implements OnInit, OnDestroy, AfterViewInit {
  public static readonly COLUMN_BORDER_WIDTH = 8;

  public static readonly rowParameterNames = {
    IGNORE_CELL_TYPE: 'ignoreSpecialCellTypes',
    ROW_DETAILS: 'details'
  };

  public readonly internalParameterNames = {
    GREYED_COLUMN_FLAG: 'GRID_ALT_COLOR_FLAG',
    COLUMN_WIDTH: 'GRID_COLUMN_WIDTH'
  };

  public dataSource: any;

  @Input()
  public dynamicRowWidth: string;

  @Input()
  public rowData: any[];

  @Input()
  public columnDefs: GridDefinitions;

  @Input()
  public outerDataSource: GridDataSource;

  @Input()
  public twoColorColumnBackgrounds = false;

  @Input()
  public disableHeader = false;

  @Input()
  public minWidth: string;

  @Input()
  public headerHeight = '38px';

  @Input()
  public paginatorFlag = false;

  @Input()
  public dataFilter: BehaviorSubject<any>;


  @ViewChild(MatPaginator)
  paginator: MatPaginator;

  @ViewChild(MatSort)
  sort: MatSort;

  public selectedRowId: number;
  private subscriptions: Subscription[] = [];

  constructor() {
  }

  public static calculateRowWidth(columns: GridColumn[]): string {
    let width = columns.map(col => col.width)
      .reduce((prev, curr) => prev + curr + GridComponent.COLUMN_BORDER_WIDTH);
    width += GridComponent.COLUMN_BORDER_WIDTH;
    return width.toString();
  }

  ngOnInit() {
    let altColor = true;
    for (const column of this.columnDefs.columns) {
      column[this.internalParameterNames.GREYED_COLUMN_FLAG] = this.twoColorColumnBackgrounds && altColor;
      column[this.internalParameterNames.COLUMN_WIDTH] = column.width ? '0 0 ' + column.width + 'px' : '';
      altColor = !altColor;
    }
    this.dataSource = this.outerDataSource ? this.outerDataSource :
      new GridDataSource(this.rowData);

    if (this.dataSource && this.dataSource.selectedRowId) {
      this.selectedRowId = this.dataSource.selectedRowId.value;
    }
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    if (this.paginatorFlag) {
      this.dataSource.paginator = this.paginator;
      if (this.outerDataSource) {
        this.subscriptions.push(this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0));
        this.subscriptions.push(this.getTableChangesObs()
          .pipe(switchMap(_ => this.getRefreshedData())
          ).subscribe(
            res => this.dataSource.onResult(res),
            err => this.dataSource.onError(err)
          ));

      }
    }
  }

  public onRowClick(rowId) {
    if (this.dataSource && this.dataSource.selectedRowId) {
      if (rowId !== null && this.selectedRowId === rowId) {
        this.selectedRowId = null;
      } else {
        this.selectedRowId = rowId;
      }
      this.dataSource.selectedRowId.next(this.selectedRowId);
    }
  }

  private getRefreshedData = () => {
    return this.dataSource.refresh({
      paginator: this.paginator,
      sort: this.sort,
      filter: this.dataFilter ? this.dataFilter.value : {}
    });
  }

  private getTableChangesObs() {
    if (this.dataFilter) {
      return merge(this.sort.sortChange, this.paginator.page, this.dataFilter);
    }
    return merge(this.sort.sortChange, this.paginator.page);
  }

}

