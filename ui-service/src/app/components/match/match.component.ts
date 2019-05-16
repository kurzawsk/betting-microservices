import {AfterViewInit, Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {GridDefinitions} from '../common/ui-grid/grid-definitions';
import {BehaviorSubject, Subject, Subscription} from 'rxjs/index';
import {Constants} from '../common/Constants';
import {SpinnerContainerComponent} from '../common/progress-spinner/container/spinner-container.component';
import {GridComponent} from '../common/ui-grid/grid.component';
import {DateCellComponent} from '../common/ui-grid/cells/date-cell/date-cell.component';
import {MatchDataSource} from './match-data-source.service';
import {MatchOddDataSource} from './match-odd-data-source.service';
import {MatchOddColumn} from './model/match-column-model';
import {Match, MatchOdd} from './model/data-model';
import {SimpleSearchComponent} from '../common/simple-search/simple-search.component';
import {FormArray, FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {MatchService} from './match.service';
import {MatchTeamLogic} from './match-team-logic';
import {ColumnConfig} from './config/match-team-column-config';

@Component({
  selector: 'app-match',
  templateUrl: './match.component.html',
  styleUrls: ['./match.component.scss']
})
export class MatchComponent implements OnInit, AfterViewInit, OnDestroy {

  public dynamicRowWidth: string;
  public matchOddsDynamicRowWidth: string;
  public gridColumnsDef: GridDefinitions;
  public matchOddsGridColumnsDef: GridDefinitions;
  public matchFilterSubject: Subject<{}> = new BehaviorSubject(this.matchDataSource.pagedSearchParams.filter);
  public matchOddFilterSubject: Subject<{}> = new BehaviorSubject(this.matchOddDataSource.pagedSearchParams.filter);
  public singleMatchData: Match;

  private subscriptions: Subscription[] = [];

  @ViewChild('matchFilterText')
  matchFilterText: SimpleSearchComponent;

  @ViewChild('matchSpinner')
  matchSpinner: SpinnerContainerComponent;

  @ViewChild('matchOddsSpinner')
  matchOddSpinner: SpinnerContainerComponent;

  form: FormGroup;


  constructor(public matchDataSource: MatchDataSource,
              public matchOddDataSource: MatchOddDataSource,
              private matchService: MatchService,
              public route: ActivatedRoute,
              private formBuilder: FormBuilder) {
    this.subscriptions.push(this.route.params.subscribe(() => this.refreshSingleMatchData()));
  }

  ngOnInit() {
    this.matchOddDataSource.prepareData = this.prepareMatchOddData;
    this.setUpColumns();

    if (!this.route.snapshot.paramMap.get('id')) {
      this.subscriptions.push(this.matchDataSource.selectedRowId
        .subscribe(matchId => this.matchOddFilterSubject.next({matchId: matchId})));
      const controls = MatchTeamLogic.RESULT_TYPES.map(_ => new FormControl(false));
      this.form = this.formBuilder.group({
        resultTypes: new FormArray(controls)
      });
      this.matchDataSource.prepareData = MatchTeamLogic.prepareMatchData;

    }
  }

  ngAfterViewInit() {
    if (!this.route.snapshot.paramMap.get('id')) {
      this.subscriptions.push(this.matchDataSource.loadingFinishedSubj
        .subscribe(v => this.matchSpinner.loadingFinishedSubj.next(v)));
      this.subscriptions.push(this.form.valueChanges.subscribe(_ =>
        this.matchFilterSubject.next(this.getCurrentFilterValue())));
      this.matchFilterText.userInputControl
        .setValue(this.matchDataSource.pagedSearchParams && this.matchDataSource.pagedSearchParams.filter ?
          this.matchDataSource.pagedSearchParams.filter.teamsName : '', {emitEvent: false});
      if (this.matchDataSource.pagedSearchParams.filter && this.matchDataSource.pagedSearchParams.filter.resultTypes) {
        const rts = MatchTeamLogic.RESULT_TYPES.map(rt => this.matchDataSource.pagedSearchParams.filter.resultTypes.includes(rt.id));
        this.form.controls.resultTypes.setValue(rts, {emitEvent: false});
      }
    }

    this.subscriptions.push(this.matchOddDataSource.loadingFinishedSubj
      .subscribe(v => {
        if (this.matchOddSpinner) {
          this.matchOddSpinner.loadingFinishedSubj.next(v);
        }
      }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  getResultTypeLabel(id) {
    return MatchTeamLogic.getResultTypeLabel(id);
  }

  getCheckedResultTypes = () => {
    return MatchTeamLogic.RESULT_TYPES
      .filter(rt => this.form.controls.resultTypes.value[MatchTeamLogic.RESULT_TYPES.indexOf(rt)])
      .map(rt => rt.id);
  }

  getAllResultTypes = () => MatchTeamLogic.RESULT_TYPES;

  protected prepareMatchOddData = (rawData: MatchOdd[]): MatchOdd[] => {
    const gridRows = [];
    for (const entry of rawData) {
      const additionalRowProps = {};
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT] = {};
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT][MatchOddColumn.UPDATED_ON] = Constants.DATE_TIME_FORMAT;
      gridRows.push(Object.assign(entry, additionalRowProps));
    }
    return gridRows;
  }

  private refreshSingleMatchData = () => {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.matchService.getMatch(Number.parseInt(id)).subscribe(res => {
        this.singleMatchData = res;
        this.matchOddFilterSubject.next({matchId: id});
      });
    }
  }

  private getCurrentFilterValue = () => {
    return {
      teamsName: this.matchFilterText.userInputControl.value || '',
      resultTypes: this.getCheckedResultTypes()
    };
  }

  private setUpColumns = () => {
    this.gridColumnsDef = new GridDefinitions(ColumnConfig.MATCH_COLUMN_CONFIG);
    this.dynamicRowWidth = GridComponent.calculateRowWidth(ColumnConfig.MATCH_COLUMN_CONFIG);
    this.matchOddsGridColumnsDef = new GridDefinitions(ColumnConfig.MATCH_ODD_COLUMN_CONFIG);
    this.matchOddsDynamicRowWidth = GridComponent.calculateRowWidth(ColumnConfig.MATCH_ODD_COLUMN_CONFIG);
  }

}
