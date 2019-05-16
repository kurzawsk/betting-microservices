import {Component, Inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {SpinnerContainerComponent} from '../common/progress-spinner/container/spinner-container.component';
import {GridComponent} from '../common/ui-grid/grid.component';
import {Subscription} from 'rxjs/index';
import {GridDefinitions} from '../common/ui-grid/grid-definitions';
import {CustomReportParameters, PredefinedPeriod, Report} from './model/data-model';
import {ReportingDataSource} from './reporting-data-source';
import {ColumnConfig} from './config/report-column-config';
import {DateCellComponent} from '../common/ui-grid/cells/date-cell/date-cell.component';
import {Constants} from '../common/Constants';
import {FormGroup} from '@angular/forms';
import {CheckboxInputCellComponent} from '../common/ui-grid/cells/checkbox-input-cell/checkbox-input-cell.component';
import {ReportColumn} from './model/column-model';
import {MAT_DIALOG_DATA, MatDatepickerInputEvent, MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material';
import {ReportingService} from './reporting.service';


@Component({
  selector: 'app-execute-reports-dialog-component',
  templateUrl: './execute-reports-dialog.html'
})
export class ExecuteReportsDialogComponent {

  public useCustomParameters;
  public PREDEFINED_PERIODS = [
    {value: PredefinedPeriod.YESTERDAY, viewValue: 'Yesterday'},
    {value: PredefinedPeriod.LAST_7_DAYS, viewValue: 'Last 7 days'},
    {value: PredefinedPeriod.LAST_MONTH, viewValue: 'Last month'}
  ];

  selectedPredefinedPeriod: PredefinedPeriod;
  fromDate: Date;
  toDate: Date;
  sendEmail: boolean;
  datesInvalidMessage: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data, public dialogRef: MatDialogRef<ExecuteReportsDialogComponent>) {
  }

  fromDateChanged(event: MatDatepickerInputEvent<Date>) {
    if (event.value) {
      this.selectedPredefinedPeriod = null;
    }
    this.validateDates();
  }

  toDateChanged(event: MatDatepickerInputEvent<Date>) {
    if (event.value) {
      this.selectedPredefinedPeriod = null;
    }
    this.validateDates();
  }

  predefinedPeriodChanged(selected) {
    if (selected) {
      this.fromDate = null;
      this.toDate = null;
    }
  }

  private validateDates() {
    if ((this.fromDate && !this.toDate) || (this.toDate && !this.fromDate)) {
      this.datesInvalidMessage = 'Please select both dates';
    } else if (this.fromDate && this.toDate && this.fromDate.getTime() >= this.toDate.getTime()) {
      this.datesInvalidMessage = 'To date has to be after from date';
    } else {
      this.datesInvalidMessage = null;
    }
  }

}

@Component({
  selector: 'app-reporting',
  templateUrl: './reporting.component.html'
})
export class ReportingComponent implements OnInit, OnDestroy {

  public dynamicRowWidth: string;
  public gridColumnsDef: GridDefinitions;

  private subscriptions: Subscription[] = [];
  private selectedForExecution: { id: number, title: string }[] = [];

  @ViewChild('reportSpinner')
  reportSpinner: SpinnerContainerComponent;

  constructor(public reportingDataSource: ReportingDataSource, private reportingService: ReportingService,
              private matDialog: MatDialog) {
  }

  ngOnInit() {
    this.reportingDataSource.prepareData = this.prepareData;
    this.setUpColumns();

    this.subscriptions.push(this.reportingDataSource.loadingFinishedSubj
      .subscribe(v => this.reportSpinner.loadingFinishedSubj.next(v)));
    this.reportingDataSource.refreshDo();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private setUpColumns = () => {
    this.gridColumnsDef = new GridDefinitions(ColumnConfig.REPORT_COLUMN_CONFIG);
    this.dynamicRowWidth = GridComponent.calculateRowWidth(ColumnConfig.REPORT_COLUMN_CONFIG);
  }

  protected prepareData = (rawData: Report[]): Report[] => {
    const gridRows = [];
    for (const entry of rawData) {
      const additionalRowProps = {};

      additionalRowProps[CheckboxInputCellComponent.parameterNames.FORM_GROUPS] = [];
      additionalRowProps[CheckboxInputCellComponent.parameterNames.FORM_CONTROL_NAMES] = [];
      additionalRowProps[CheckboxInputCellComponent.parameterNames.IS_DISABLED] = [];
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT] = {};
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT][ReportColumn.LAST_EXECUTION_START_TIME] =
        Constants.DATE_TIME_FORMAT;
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT][ReportColumn.LAST_EXECUTION_FINISH_TIME] =
        Constants.DATE_TIME_FORMAT;

      const fg = new FormGroup({});
      additionalRowProps[CheckboxInputCellComponent.parameterNames.FORM_GROUPS][ReportColumn.ENABLED] = fg;
      additionalRowProps[CheckboxInputCellComponent.parameterNames.FORM_CONTROL_NAMES][ReportColumn.ENABLED] = 'checked';
      additionalRowProps[CheckboxInputCellComponent.parameterNames.IS_DISABLED][ReportColumn.ENABLED] = false;
      fg.valueChanges.subscribe(val => {
        if (val.checked !== entry.enabled) {
          this.reportingService.toggleReportEnabled(entry.id, val.checked)
            .subscribe(_ => this.reportingDataSource.refreshDo());
        }
      });

      const fgExecute = new FormGroup({});
      additionalRowProps[CheckboxInputCellComponent.parameterNames.FORM_GROUPS][ReportColumn.EXECUTE] = fgExecute;
      additionalRowProps[CheckboxInputCellComponent.parameterNames.FORM_CONTROL_NAMES][ReportColumn.EXECUTE] = 'checked';
      additionalRowProps[CheckboxInputCellComponent.parameterNames.IS_DISABLED][ReportColumn.EXECUTE] = false;
      fgExecute.valueChanges.subscribe(val => {
        if (val.checked) {
          this.selectedForExecution.push({id: entry.id, title: entry.title});
        } else {
          console.log('deleting: ');
          this.selectedForExecution
            .splice(this.selectedForExecution.indexOf(this.selectedForExecution.find(e => e.id === entry.id)), 1);
        }
        console.log('state = ' + JSON.stringify(Array.from(this.selectedForExecution)));
      });


      gridRows.push(Object.assign(entry, additionalRowProps));
    }
    return gridRows;

  }

  showRunReportsDialog = () => {
    if (this.selectedForExecution.length === 0) {
      window.alert('Please select at least one report to execute');
      return;
    }

    const dialogConfig = new MatDialogConfig();
    dialogConfig.maxWidth = '90%';
    dialogConfig.closeOnNavigation = true;
    dialogConfig.maxHeight = 1200;
    dialogConfig.data = {reports: this.selectedForExecution};
    this.matDialog.open(ExecuteReportsDialogComponent, dialogConfig)
      .afterClosed()
      .subscribe(result => {
        if (result) {
          let customReportParameters: CustomReportParameters;
          if (result.fromDate || result.toDate || result.selectedPredefinedPeriod) {
            customReportParameters = {
              fromDate: result.fromDate,
              toDate: result.toDate,
              predefinedPeriod: result.selectedPredefinedPeriod
            };
          }

          console.log('customReportParameters = ' + customReportParameters);
          const ids = this.selectedForExecution.map(sel => sel.id);
          this.reportingService.executeReports(ids, customReportParameters, result.sendEmail).subscribe(_ => {
            this.selectedForExecution.length = 0;
            this.reportingDataSource.refreshDo();
          });
        }
      });
  }
}

