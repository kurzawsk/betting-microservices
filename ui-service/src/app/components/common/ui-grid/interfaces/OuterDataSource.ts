export interface OuterDataSource {

  refresh({paginator: MatPaginator, sort: MatSort, selectedRowId: number, filter: any}): void;

  onResult(args: any): any;

  onError(args: any): any;

}
