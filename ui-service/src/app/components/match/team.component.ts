import {AfterViewInit, Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {MatchService} from './match.service';
import {ActivatedRoute} from '@angular/router';
import {BehaviorSubject, Subject, Subscription} from 'rxjs/index';
import {GridDefinitions} from '../common/ui-grid/grid-definitions';
import {TeamDataSource} from './team-data-source.service';
import {SimpleSearchComponent} from '../common/simple-search/simple-search.component';
import {SpinnerContainerComponent} from '../common/progress-spinner/container/spinner-container.component';
import {GridComponent} from '../common/ui-grid/grid.component';
import {RouterLinkCellComponent} from '../common/ui-grid/cell';
import {MatchColumn} from './model/match-column-model';
import {SplitCellComponent} from '../common/ui-grid/cells/split-cell/split-cell.component';
import {TeamMatchesDataSource} from './team-matches-data-source.service';
import {MatchTeamLogic} from './match-team-logic';
import {ColumnConfig} from './config/match-team-column-config';
import {skip} from 'rxjs/internal/operators';
import {Team} from './model/data-model';

@Component({
  selector: 'app-team',
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.scss']
})
export class TeamComponent implements OnInit, OnDestroy, AfterViewInit {
  public dynamicRowWidth: string;
  public teamMatchesDynamicRowWidth: string;
  public gridColumnsDef: GridDefinitions;
  public teamMatchesGridColumnsDef: GridDefinitions;
  public teamFilterSubject: Subject<{}> = new BehaviorSubject(this.teamDataSource.pagedSearchParams.filter);
  public teamMatchesFilterSubject: Subject<{}> = new BehaviorSubject(this.teamMatchesDataSource.pagedSearchParams.filter);

  public singleTeamData: Team;
  private subscriptions: Subscription[] = [];

  @ViewChild('teamFilterText')
  teamFilterText: SimpleSearchComponent;

  @ViewChild('teamSpinner')
  teamSpinner: SpinnerContainerComponent;

  @ViewChild('teamMatchesSpinner')
  teamMatchesSpinner: SpinnerContainerComponent;

  constructor(public teamDataSource: TeamDataSource,
              public teamMatchesDataSource: TeamMatchesDataSource,
              private matchService: MatchService,
              public route: ActivatedRoute) {
    this.subscriptions.push(this.route.params.subscribe(() => this.refreshSingleTeamData()));
  }

  ngOnInit() {
    this.teamMatchesDataSource.prepareData = MatchTeamLogic.prepareMatchData;
    this.setUpColumns();

    if (!this.route.snapshot.paramMap.get('id')) {
      this.teamDataSource.prepareData = this.prepareData;
      this.subscriptions.push(this.teamDataSource.selectedRowId
        .subscribe(teamId => this.teamMatchesFilterSubject.next({teamId: teamId})));
    }
  }

  ngAfterViewInit() {
    if (!this.route.snapshot.paramMap.get('id')) {
      this.subscriptions.push(this.teamDataSource.loadingFinishedSubj
        .subscribe(v => this.teamSpinner.loadingFinishedSubj.next(v)));

      this.subscriptions.push(this.teamFilterText.userInputControl.valueChanges.subscribe(_ =>
        this.teamFilterSubject.next(this.getCurrentFilterValue())));

      this.teamFilterText.userInputControl
        .setValue(this.teamDataSource.pagedSearchParams && this.teamDataSource.pagedSearchParams.filter ?
          this.teamDataSource.pagedSearchParams.filter.name : '', {emitEvent: false});
    }

    this.subscriptions.push(this.teamMatchesDataSource.loadingFinishedSubj
      .subscribe(v => {
        if (this.teamMatchesSpinner) {
          this.teamMatchesSpinner.loadingFinishedSubj.next(v);
        }
      }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private refreshSingleTeamData = () => {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.matchService.getTeam(Number.parseInt(id)).subscribe(res => {
        this.singleTeamData = res;
        this.teamMatchesFilterSubject.next({teamId: id});
      });
    }
  }

  private setUpColumns = () => {
    this.gridColumnsDef = new GridDefinitions(ColumnConfig.TEAM_COLUMN_CONFIG);
    this.dynamicRowWidth = GridComponent.calculateRowWidth(ColumnConfig.TEAM_COLUMN_CONFIG);
    this.teamMatchesGridColumnsDef = new GridDefinitions(ColumnConfig.MATCH_COLUMN_CONFIG);
    this.teamMatchesDynamicRowWidth = GridComponent.calculateRowWidth(ColumnConfig.MATCH_COLUMN_CONFIG);
  }

  protected prepareData = (rawData: Team[]): Team[] => {
    const gridRows = [];
    for (const entry of rawData) {
      const additionalRowProps = {};

      additionalRowProps[RouterLinkCellComponent.parameterNames.ROUTER_LINKS] = {};
      additionalRowProps[RouterLinkCellComponent.parameterNames.ROUTER_LINKS][MatchColumn.ID] = '/team/' + entry.id;
      additionalRowProps[SplitCellComponent.parameterNames.DIVIDED] = true;

      gridRows.push(Object.assign(entry, additionalRowProps));
    }
    return gridRows;
  }

  private getCurrentFilterValue = () => {
    return {
      name: this.teamFilterText.userInputControl.value || ''
    };
  }

}
