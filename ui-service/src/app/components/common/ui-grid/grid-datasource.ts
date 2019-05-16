import {MatTableDataSource} from '@angular/material';
import {GridColumn} from './grid-definitions';

export class GridDataSource extends MatTableDataSource<any> {


  constructor(rowData) {
    super([]);
    this.data = rowData;
  }

  _updatePaginator(filteredDataLength: number) {
    super._updatePaginator(filteredDataLength);
  }

  _pageData(data: any[]) {
    if (!this.paginator) {
      return data;
    }
    const startIndex = this.paginator.pageIndex * this.paginator.pageSize * 2;
    return data.slice().splice(startIndex, this.paginator.pageSize * 2);
  }

  serialize(columns: GridColumn[]): void {
  }

  _filterData(data: any[]) {
    return super._filterData(data);
  }

  _orderData(data: any[]) {
    if (!this.sort || !this.sort.active) {
      return super._orderData(data);
    }
    return this.getRows();
  }

  private getRows() {
    const rows = [];
    for (const row of this.filteredData) {
      rows.push(row);
    }
    return rows;
  }

}
