import {ExpandableTextCellComponent, MultiColorCellComponent, TextCellComponent} from '../../common/ui-grid/cell';
import {ButtonCellComponent} from '../../common/ui-grid/cells/button-cell/button-cell.component';
import {DateCellComponent} from '../../common/ui-grid/cells/date-cell/date-cell.component';
import {CheckboxInputCellComponent} from '../../common/ui-grid/cells/checkbox-input-cell/checkbox-input-cell.component';
import {CellTypeName, GridColumn} from '../../common/ui-grid/grid-definitions';
import {JobColumn, JobExecutionColumn} from '../model/job-column-model';

export class ColumnConfig {

  public static readonly JOB_COLUMN_CONFIG: GridColumn[] = [
    {
      name: JobColumn.ID,
      label: 'Id',
      type: TextCellComponent,
      sortable: true,
      width: 90
    },
    {
      name: JobColumn.CODE,
      label: 'Code',
      type: TextCellComponent,
      sortable: true,
      width: 190
    },
    {
      name: JobColumn.DESCRIPTION,
      label: 'Description',
      type: TextCellComponent,
      sortable: true,
      width: 200
    },
    {
      name: JobColumn.SERVICE_NAME,
      label: 'Service Name',
      type: TextCellComponent,
      sortable: true,
      width: 150
    },
    {
      name: JobColumn.URL_SUFFIX,
      label: 'Url Suffix',
      type: TextCellComponent,
      sortable: true,
      width: 180
    },
    {
      name: JobColumn.LAST_EXECUTION_START_TIME,
      label: 'Last Execution Start Time',
      type: DateCellComponent,
      sortable: true,
      width: 150
    },
    {
      name: JobColumn.LAST_EXECUTION_FINISH_TIME,
      label: 'Last Execution Finish Time',
      type: DateCellComponent,
      sortable: true,
      width: 150
    },
    {
      name: JobColumn.LAST_EXECUTION_JOB_STATUS,
      label: 'Last Execution Job Status',
      type: MultiColorCellComponent,
      typeName: CellTypeName.MultiColorCellComponent,
      sortable: true,
      width: 120
    },
    {
      name: JobColumn.LAST_EXECUTION_ERROR_MESSAGE,
      label: 'Last Execution Error Message',
      type: ExpandableTextCellComponent,
      width: 180
    },
    {
      name: JobColumn.ENABLED,
      label: 'Enabled',
      type: CheckboxInputCellComponent,
      width: 55
    },
    {
      name: JobColumn.RUN_BTN,
      label: 'Run',
      type: ButtonCellComponent,
      width: 50
    }];

  public static readonly JOB_EXECUTION_COLUMN_CONFIG: GridColumn[] = [
    {
      name: JobExecutionColumn.ID,
      label: 'Id',
      type: TextCellComponent,
      width: 90
    },
    {
      name: JobExecutionColumn.START_TIME,
      label: 'Start Time',
      type: DateCellComponent,
      sortable: true,
      width: 170
    },
    {
      name: JobExecutionColumn.FINISH_TIME,
      label: 'Finish Time',
      type: DateCellComponent,
      width: 170
    },
    {
      name: JobExecutionColumn.JOB_EXECUTION_STATUS,
      label: 'Job Execution Status',
      type: MultiColorCellComponent,
      typeName: CellTypeName.MultiColorCellComponent,
      width: 120
    },
    {
      name: JobExecutionColumn.ERROR_MESSAGE,
      label: 'Error Message',
      type: ExpandableTextCellComponent,
      width: 450
    }
  ];
}
