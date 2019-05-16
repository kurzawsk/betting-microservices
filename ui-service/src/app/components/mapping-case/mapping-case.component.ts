import {AfterViewInit, Component, Inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {GridDefinitions} from '../common/ui-grid/grid-definitions';
import {BehaviorSubject, Subject, Subscription} from 'rxjs/index';
import {SpinnerContainerComponent} from '../common/progress-spinner/container/spinner-container.component';
import {MappingCaseDataSource} from './mapping-case-datasource.service';
import {MappingCase, MappingCaseStatus} from './model/data-model';

import {GridComponent} from '../common/ui-grid/grid.component';
import {ColumnConfig} from './config/column-config';
import {MultiColorCellComponent, RouterLinkCellComponent} from '../common/ui-grid/cell';
import {MappingCaseColumn} from './model/mapping-case-column-model';
import {ActivatedRoute} from '@angular/router';
import {ButtonCellComponent} from '../common/ui-grid/cells/button-cell/button-cell.component';
import {Constants} from '../common/Constants';
import {DateCellComponent} from '../common/ui-grid/cells/date-cell/date-cell.component';
import {MAT_DIALOG_DATA, MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material';
import {MappingCaseService} from './mapping-case.service';
import {FormArray, FormBuilder, FormControl, FormGroup} from '@angular/forms';


@Component({
  selector: 'app-accept-reject-mapping-case-component',
  template: '<mat-dialog-content>' +
  '<p>You are about to {{action}} mapping case {{data.mappingCaseId}}.<br>Do you want to continue?<p><br>' +
  '</mat-dialog-content>' +
  '  <button mat-raised-button [mat-dialog-close]=\'true\'>OK</button>\n' +
  '  <button mat-raised-button [mat-dialog-close]=\'false\'>Cancel</button>'
})
export class AcceptRejectComponent {


  public static readonly TARGET_STATUS_2_ACTION_NAME: Map<MappingCaseStatus, string> = new Map([
    [MappingCaseStatus.ACCEPTED, 'accept'],
    [MappingCaseStatus.REJECTED, 'reject']
  ]);

  public action: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data, public dialogRef: MatDialogRef<AcceptRejectComponent>) {
    this.action = AcceptRejectComponent.TARGET_STATUS_2_ACTION_NAME.get(this.data.targetStatus);
  }
}

@Component({
  selector: 'app-mapping-case',
  templateUrl: './mapping-case.component.html',
  styleUrls: ['./mapping-case.component.scss']
})
export class MappingCaseComponent implements OnInit, AfterViewInit, OnDestroy {

  public readonly STATUS_LABELS = [
    {id: MappingCaseStatus.NEW, label: 'New'},
    {id: MappingCaseStatus.ACCEPTED, label: 'Accepted'},
    {id: MappingCaseStatus.REJECTED, label: 'Rejected'}
  ];
  public dynamicRowWidth: string;
  public gridColumnsDef: GridDefinitions;
  public mappingCaseFilterSubject: Subject<{}> = new BehaviorSubject(this.mappingCaseDataSource.pagedSearchParams.filter);
  private subscriptions: Subscription[] = [];

  @ViewChild('mappingCaseSpinner')
  mappingCaseSpinner: SpinnerContainerComponent;

  form: FormGroup;


  constructor(public mappingCaseDataSource: MappingCaseDataSource,
              public route: ActivatedRoute,
              private mappingCaseService: MappingCaseService,
              private acceptRejectDialog: MatDialog,
              private formBuilder: FormBuilder) {
  }

  ngOnInit() {
    this.mappingCaseDataSource.prepareData = this.prepareData;
    this.setUpColumns();

    const controls = Array.from(this.STATUS_LABELS.keys()).map(_ => new FormControl(true));
    this.form = this.formBuilder.group({
      statuses: new FormArray(controls)
    });

  }

  ngAfterViewInit() {
    this.subscriptions.push(this.mappingCaseDataSource.loadingFinishedSubj
      .subscribe(v => this.mappingCaseSpinner.loadingFinishedSubj.next(v)));

    this.subscriptions.push(this.form.valueChanges.subscribe(_ =>
      this.mappingCaseFilterSubject.next(this.getCurrentFilterValue())));

    if (this.mappingCaseDataSource.pagedSearchParams.filter && this.mappingCaseDataSource.pagedSearchParams.filter.statuses) {
      const sts = this.STATUS_LABELS.map(s => this.mappingCaseDataSource.pagedSearchParams.filter.statuses.includes(s.id));
      this.form.controls.statuses.setValue(sts, {emitEvent: false});
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  get statusesControl() {
    return this.form.controls.statuses['controls'];
  }

  protected prepareData = (rawData: MappingCase[]): MappingCase[] => {
    const gridRows = [];
    for (const entry of rawData) {
      const additionalRowProps = {};

      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT] = {};
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT][MappingCaseColumn.CREATED_ON] = Constants.DATE_TIME_FORMAT;
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT][MappingCaseColumn.UPDATED_ON] = Constants.DATE_TIME_FORMAT;

      additionalRowProps[RouterLinkCellComponent.parameterNames.ROUTER_LINKS] = {};
      additionalRowProps[RouterLinkCellComponent.parameterNames.ROUTER_LINKS][MappingCaseColumn.MATCH_TEAMS] = '/match/' + entry.matchId;

      additionalRowProps[MappingCaseColumn.STATUS] = this.STATUS_LABELS.find(s => entry.status === s.id).label;
      additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS] = {};
      switch (entry.status) {
        case MappingCaseStatus.ACCEPTED:
          additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS][MappingCaseColumn.STATUS] = 'green';
          break;
        case MappingCaseStatus.NEW:
          additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS][MappingCaseColumn.STATUS] = 'blue';
          break;
        case MappingCaseStatus.REJECTED:
          additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS][MappingCaseColumn.STATUS] = 'red';
          break;
      }


      additionalRowProps[ButtonCellComponent.parameterNames.BUTTON_ACTIONS] = [];
      additionalRowProps[ButtonCellComponent.parameterNames.BUTTON_PARAMS] = [];
      additionalRowProps[ButtonCellComponent.parameterNames.BUTTON_TYPES] = [];
      additionalRowProps[ButtonCellComponent.parameterNames.DISABLED] = [];
      additionalRowProps[ButtonCellComponent.parameterNames.ICON_NAMES] = [];
      additionalRowProps[ButtonCellComponent.parameterNames.TOOLTIP] = [];


      additionalRowProps[ButtonCellComponent.parameterNames.BUTTON_ACTIONS][MappingCaseColumn.ACCEPT] =
        () => this.openAcceptRejectDialog(Number.parseInt(entry.id), MappingCaseStatus.ACCEPTED);
      additionalRowProps[ButtonCellComponent.parameterNames.BUTTON_TYPES][MappingCaseColumn.ACCEPT]
        = ButtonCellComponent.buttonType.MAT_ICON;
      additionalRowProps[ButtonCellComponent.parameterNames.ICON_NAMES][MappingCaseColumn.ACCEPT] = 'done';
      additionalRowProps[ButtonCellComponent.parameterNames.TOOLTIP][MappingCaseColumn.ACCEPT] = 'Accept';
      additionalRowProps[ButtonCellComponent.parameterNames.DISABLED][MappingCaseColumn.ACCEPT]
        = (entry.status !== MappingCaseStatus.NEW);

      additionalRowProps[ButtonCellComponent.parameterNames.BUTTON_ACTIONS][MappingCaseColumn.REJECT] =
        () => this.openAcceptRejectDialog(Number.parseInt(entry.id), MappingCaseStatus.REJECTED);
      additionalRowProps[ButtonCellComponent.parameterNames.BUTTON_TYPES][MappingCaseColumn.REJECT]
        = ButtonCellComponent.buttonType.MAT_ICON;
      additionalRowProps[ButtonCellComponent.parameterNames.ICON_NAMES][MappingCaseColumn.REJECT] = 'clear';
      additionalRowProps[ButtonCellComponent.parameterNames.TOOLTIP][MappingCaseColumn.REJECT] = 'Reject';
      additionalRowProps[ButtonCellComponent.parameterNames.DISABLED][MappingCaseColumn.REJECT]
        = (entry.status !== MappingCaseStatus.NEW);

      gridRows.push(Object.assign(entry, additionalRowProps));
    }
    return gridRows;
  }

  private openAcceptRejectDialog = (mappingCaseId: number, targetStatus: MappingCaseStatus) => {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.maxWidth = '30%';
    dialogConfig.closeOnNavigation = true;
    dialogConfig.minHeight = 100;
    dialogConfig.maxHeight = 2000;
    dialogConfig.data = {mappingCaseId: mappingCaseId, targetStatus: targetStatus};
    this.acceptRejectDialog.open(AcceptRejectComponent, dialogConfig).afterClosed()
      .subscribe(result => {
          if (result) {
            if (targetStatus === MappingCaseStatus.ACCEPTED) {
              this.mappingCaseService.accept(mappingCaseId)
                .subscribe(_ => this.mappingCaseDataSource.refreshDo(), err => alert(err));
            } else if (targetStatus === MappingCaseStatus.REJECTED) {
              this.mappingCaseService.reject(mappingCaseId)
                .subscribe(_ => this.mappingCaseDataSource.refreshDo(), err => alert(err));
            }
          }
        }
      );
  }

  private getCurrentFilterValue = () => {
    return {
      statuses: this.STATUS_LABELS
        .filter(s => this.form.controls.statuses.value[this.STATUS_LABELS.indexOf(s)])
        .map(s => s.id)
    };
  }

  private setUpColumns = () => {
    this.gridColumnsDef = new GridDefinitions(ColumnConfig.MAPPING_CASE_COLUMN_CONFIG);
    this.dynamicRowWidth = GridComponent.calculateRowWidth(ColumnConfig.MAPPING_CASE_COLUMN_CONFIG);
  }

}
