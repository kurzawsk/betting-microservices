import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {GridComponent} from '../common/ui-grid/grid.component';
import {DateCellComponent} from '../common/ui-grid/cells/date-cell/date-cell.component';
import {GridDefinitions} from '../common/ui-grid/grid-definitions';
import {MultiColorCellComponent} from '../common/ui-grid/cell';
import {JobDataSource} from './job-data-source';
import {ButtonCellComponent} from '../common/ui-grid/cells/button-cell/button-cell.component';
import {Subscription} from 'rxjs';
import {Job, JobExecution, JobExecutionStatus, JobOperation} from '../common/model/data-model';
import {Constants} from '../common/Constants';
import {JobService} from './job.service';
import {JobExecutionDataSource} from './job-execution-data-source';
import {CheckboxInputCellComponent} from '../common/ui-grid/cells/checkbox-input-cell/checkbox-input-cell.component';
import {FormGroup} from '@angular/forms';
import {ColumnConfig} from './config/job-column-config';
import {JobColumn, JobExecutionColumn} from './model/job-column-model';
import {SpinnerContainerComponent} from '../common/progress-spinner/container/spinner-container.component';
import {BehaviorSubject, interval, Subject} from 'rxjs/index';

@Component({
  selector: 'app-job',
  templateUrl: './job.component.html'
})
export class JobComponent implements OnInit, OnDestroy {

  private static readonly REFRESH_PERIOD = 30000;
  public dynamicRowWidth: string;
  public jobExecutionsDynamicRowWidth: string;
  public gridColumnsDef: GridDefinitions;
  public jobExecutionsGridColumnsDef: GridDefinitions;
  public jobExecutionFilterSubject: Subject<{}> = new BehaviorSubject(this.jobExecutionDataSource.pagedSearchParams.filter);

  private subscriptions: Subscription[] = [];


  @ViewChild('jobSpinner')
  jobSpinner: SpinnerContainerComponent;

  @ViewChild('jobExecutionSpinner')
  jobExecutionSpinner: SpinnerContainerComponent;

  constructor(public jobDataSource: JobDataSource,
              public jobExecutionDataSource: JobExecutionDataSource,
              private jobService: JobService) {
  }

  ngOnInit() {
    this.jobDataSource.prepareData = this.prepareData;
    this.jobExecutionDataSource.prepareData = this.prepareJobExecutionData;
    this.setUpColumns();

    this.subscriptions.push(this.jobDataSource.selectedRowId
      .subscribe(jobId => this.jobExecutionFilterSubject.next({jobId: jobId})));

    this.subscriptions.push(interval(JobComponent.REFRESH_PERIOD).subscribe(_ => this.refresh()));
    this.subscriptions.push(this.jobDataSource.loadingFinishedSubj
      .subscribe(v => this.jobSpinner.loadingFinishedSubj.next(v)));
    this.subscriptions.push(this.jobExecutionDataSource.loadingFinishedSubj
      .subscribe(v => {
        if (this.jobExecutionSpinner) {
          this.jobExecutionSpinner.loadingFinishedSubj.next(v);
        }
      }));
    this.refresh();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private setUpColumns = () => {
    this.gridColumnsDef = new GridDefinitions(ColumnConfig.JOB_COLUMN_CONFIG);
    this.dynamicRowWidth = GridComponent.calculateRowWidth(ColumnConfig.JOB_COLUMN_CONFIG);
    this.jobExecutionsGridColumnsDef = new GridDefinitions(ColumnConfig.JOB_EXECUTION_COLUMN_CONFIG);
    this.jobExecutionsDynamicRowWidth = GridComponent.calculateRowWidth(ColumnConfig.JOB_EXECUTION_COLUMN_CONFIG);
  }

  protected prepareData = (rawData: Job[]): Job[] => {
    const gridRows = [];
    for (const entry of rawData) {
      const additionalRowProps = {};
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT] = {};
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT][JobColumn.LAST_EXECUTION_START_TIME] = Constants.DATE_TIME_FORMAT;
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT][JobColumn.LAST_EXECUTION_FINISH_TIME] = Constants.DATE_TIME_FORMAT;

      additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS] = {};
      switch (entry.lastExecutionJobStatus) {
        case JobExecutionStatus.SUCCESS:
          additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS][JobColumn.LAST_EXECUTION_JOB_STATUS] = 'green';
          break;
        case JobExecutionStatus.RUNNING:
          additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS][JobColumn.LAST_EXECUTION_JOB_STATUS] = 'blue';
          break;
        case JobExecutionStatus.FAILED:
          additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS][JobColumn.LAST_EXECUTION_JOB_STATUS] = 'red';
          break;
      }

      additionalRowProps[ButtonCellComponent.parameterNames.BUTTON_ACTIONS] = [];
      additionalRowProps[ButtonCellComponent.parameterNames.BUTTON_PARAMS] = [];
      additionalRowProps[ButtonCellComponent.parameterNames.BUTTON_TYPES] = [];
      additionalRowProps[ButtonCellComponent.parameterNames.DISABLED] = [];
      additionalRowProps[ButtonCellComponent.parameterNames.ICON_NAMES] = [];
      additionalRowProps[ButtonCellComponent.parameterNames.TOOLTIP] = [];
      additionalRowProps[CheckboxInputCellComponent.parameterNames.FORM_GROUPS] = [];
      additionalRowProps[CheckboxInputCellComponent.parameterNames.FORM_CONTROL_NAMES] = [];
      additionalRowProps[CheckboxInputCellComponent.parameterNames.IS_DISABLED] = [];

      additionalRowProps[ButtonCellComponent.parameterNames.BUTTON_ACTIONS][JobColumn.RUN_BTN] =
        () => {
          this.jobSpinner.loadingFinishedSubj.next(false);
          this.jobService.runJob(entry.id).subscribe(_ => {
              this.jobSpinner.loadingFinishedSubj.next(true);
              this.refresh();
            },
            ex => {
              this.jobSpinner.loadingFinishedSubj.next(true);
              alert('Error running job: ' + JSON.stringify(ex.error));
            });
        };
      additionalRowProps[ButtonCellComponent.parameterNames.BUTTON_TYPES][JobColumn.RUN_BTN]
        = ButtonCellComponent.buttonType.MAT_ICON;
      additionalRowProps[ButtonCellComponent.parameterNames.ICON_NAMES][JobColumn.RUN_BTN] = 'play_circle_outline';
      additionalRowProps[ButtonCellComponent.parameterNames.TOOLTIP][JobColumn.RUN_BTN] = 'Run Job';


      additionalRowProps[ButtonCellComponent.parameterNames.DISABLED][JobColumn.RUN_BTN]
        = (entry.lastExecutionJobStatus === JobExecutionStatus.RUNNING || entry.enabled === false);

      const fg = new FormGroup({});
      additionalRowProps[CheckboxInputCellComponent.parameterNames.FORM_GROUPS][JobColumn.ENABLED] = fg;
      additionalRowProps[CheckboxInputCellComponent.parameterNames.FORM_CONTROL_NAMES][JobColumn.ENABLED] = 'enabled';
      additionalRowProps[CheckboxInputCellComponent.parameterNames.IS_DISABLED][JobColumn.ENABLED] = false;
      fg.valueChanges.subscribe(val => {
        if (val.enabled !== entry.enabled) {
          const operation = val.enabled ? JobOperation.ENABLE_JOB : JobOperation.DISABLE_JOB;
          this.jobService.toggleJob(entry.id, operation).subscribe(_ => {
              this.refresh();
            },
            ex => alert('Error toggling job: ' + JSON.stringify(ex.error)));
        }
      });

      gridRows.push(Object.assign(entry, additionalRowProps));
    }
    return gridRows;
  }

  protected prepareJobExecutionData = (rawData: JobExecution[]): JobExecution[] => {
    const gridRows = [];
    for (const entry of rawData) {
      const additionalRowProps = {};
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT] = {};
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT][JobExecutionColumn.START_TIME] = Constants.DATE_TIME_FORMAT;
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT][JobExecutionColumn.FINISH_TIME] = Constants.DATE_TIME_FORMAT;

      additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS] = {};
      switch (entry.jobExecutionStatus) {
        case JobExecutionStatus.SUCCESS:
          additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS][JobExecutionColumn.JOB_EXECUTION_STATUS] = 'green';
          break;
        case JobExecutionStatus.RUNNING:
          additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS][JobExecutionColumn.JOB_EXECUTION_STATUS] = 'blue';
          break;
        case JobExecutionStatus.FAILED:
          additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS][JobExecutionColumn.JOB_EXECUTION_STATUS] = 'red';
          break;
      }

      gridRows.push(Object.assign(entry, additionalRowProps));
    }
    return gridRows;
  }

  private refresh = () => {
    this.jobDataSource.refreshDo();
    this.jobExecutionDataSource.refreshDo();
  }

}
