import {CheckboxCellComponent, ExpandableTextCellComponent, TextCellComponent} from '../../common/ui-grid/cell';
import {DateCellComponent} from '../../common/ui-grid/cells/date-cell/date-cell.component';
import {GridColumn} from '../../common/ui-grid/grid-definitions';
import {ReportColumn} from '../model/column-model';
import {CheckboxInputCellComponent} from '../../common/ui-grid/cells/checkbox-input-cell/checkbox-input-cell.component';

export class ColumnConfig {

  public static readonly REPORT_COLUMN_CONFIG: GridColumn[] = [
    {
      name: ReportColumn.ID,
      label: 'Id',
      type: TextCellComponent,
      sortable: true,
      width: 30
    },
    {
      name: ReportColumn.CODE,
      label: 'Code',
      type: TextCellComponent,
      sortable: true,
      width: 150
    },
    {
      name: ReportColumn.TITLE,
      label: 'Title',
      type: TextCellComponent,
      sortable: true,
      width: 190
    },
    {
      name: ReportColumn.DESCRIPTION,
      label: 'Description',
      type: ExpandableTextCellComponent,
      sortable: true,
      width: 100
    },
    {
      name: ReportColumn.SERVICE_NAME,
      label: 'Service Name',
      type: TextCellComponent,
      sortable: true,
      width: 130
    },
    {
      name: ReportColumn.URL_SUFFIX,
      label: 'Url Suffix',
      type: TextCellComponent,
      sortable: true,
      width: 130
    },
    {
      name: ReportColumn.DEFAULT_PARAMETERS,
      label: 'Default Parameters',
      type: ExpandableTextCellComponent,
      width: 120
    },
    {
      name: ReportColumn.LAST_EXECUTION_START_TIME,
      label: 'Last Execution Start Time',
      type: DateCellComponent,
      sortable: true,
      width: 130
    },
    {
      name: ReportColumn.LAST_EXECUTION_FINISH_TIME,
      label: 'Last Execution Finish Time',
      type: DateCellComponent,
      sortable: true,
      width: 130
    },
    {
      name: ReportColumn.LAST_EXECUTION_RESULT_DATA,
      label: 'Last Execution Data',
      type: ExpandableTextCellComponent,
      width: 180
    },
    {
      name: ReportColumn.LAST_EXECUTION_PARAMETERS,
      label: 'Last Execution Parameters',
      type: ExpandableTextCellComponent,
      width: 120
    },
    {
      name: ReportColumn.ENABLED,
      label: 'Enabled',
      type: CheckboxInputCellComponent,
      width: 55
    },
    {
      name: ReportColumn.EXECUTE,
      label: 'Execute',
      type: CheckboxInputCellComponent,
      width: 50
    }
  ];

}
