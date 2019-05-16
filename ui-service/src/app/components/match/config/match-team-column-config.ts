import {DateCellComponent} from '../../common/ui-grid/cells/date-cell/date-cell.component';
import {MultiColorCellComponent, RouterLinkCellComponent, TextCellComponent} from '../../common/ui-grid/cell';
import {MatchColumn, MatchOddColumn} from '../model/match-column-model';
import {GridColumn} from '../../common/ui-grid/grid-definitions';
import {TeamColumn} from '../model/team-column-model';
import {SplitCellComponent} from '../../common/ui-grid/cells/split-cell/split-cell.component';

export class ColumnConfig {
  public static readonly MATCH_COLUMN_CONFIG: GridColumn[] = [
    {
      name: MatchColumn.ID,
      label: 'Id',
      type: RouterLinkCellComponent,
      sortable: true,
      width: 90
    },
    {
      name: MatchColumn.HOME_TEAM_NAME,
      label: 'Home Team',
      type: RouterLinkCellComponent,
      sortable: false,
      width: 170
    },
    {
      name: MatchColumn.AWAY_TEAM_NAME,
      label: 'Away Team',
      type: RouterLinkCellComponent,
      sortable: false,
      width: 170
    },
    {
      name: MatchColumn.START_TIME,
      label: 'Start time',
      type: DateCellComponent,
      sortable: true,
      width: 130
    },
    {
      name: MatchColumn.RESULT_TYPE_LBL,
      label: 'Result Type',
      type: MultiColorCellComponent,
      sortable: true,
      width: 90
    },
    {
      name: MatchColumn.RESULT,
      label: 'Result',
      type: TextCellComponent,
      sortable: false,
      width: 50
    },
    {
      name: MatchColumn.SOURCE_SYSTEM_NAME,
      label: 'Source system',
      type: TextCellComponent,
      sortable: false,
      width: 160
    },
    {
      name: MatchColumn.SOURCE_SYSTEM_ID,
      label: 'Source system Id',
      type: TextCellComponent,
      sortable: false,
      width: 120
    },
    {
      name: MatchColumn.MARKED_AS_FINISHED_TIME,
      label: 'Marked as finished',
      type: DateCellComponent,
      sortable: false,
      width: 130
    }];

  public static readonly MATCH_ODD_COLUMN_CONFIG: GridColumn[] = [
    {
      name: MatchOddColumn.BOOKMAKER_NAME,
      label: 'Bookmaker',
      type: TextCellComponent,
      width: 110
    },
    {
      name: MatchOddColumn.ODD1,
      label: '1',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDX,
      label: 'X',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODD2,
      label: '2',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODD1X,
      label: '1X',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDX2,
      label: 'X2',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODD12,
      label: '12',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDBTSN,
      label: 'BTSN',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDBTSY,
      label: 'BTSY',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDO05,
      label: 'o 0.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDU05,
      label: 'u 0.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDO15,
      label: 'o 1.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDU15,
      label: 'u 1.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDO25,
      label: 'o 2.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDU25,
      label: 'u 2.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDO35,
      label: 'o 3.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDU35,
      label: 'u 3.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDO45,
      label: 'o 4.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDU45,
      label: 'u 4.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDO55,
      label: 'o 5.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDU55,
      label: 'u 5.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDO65,
      label: 'o 6.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.ODDU65,
      label: 'u 6.5',
      type: TextCellComponent,
      width: 35
    },
    {
      name: MatchOddColumn.UPDATED_ON,
      label: 'Updated on',
      type: DateCellComponent,
      width: 150
    }
  ];

  public static readonly TEAM_COLUMN_CONFIG: GridColumn[] = [
    {
      name: TeamColumn.ID,
      label: 'Id',
      type: RouterLinkCellComponent,
      sortable: true,
      width: 90
    },
    {
      name: TeamColumn.NAME,
      label: 'Name',
      type: TextCellComponent,
      sortable: false,
      width: 200
    },
    {
      name: TeamColumn.ALTERNATIVE_NAMES,
      label: 'Alternative names',
      type: SplitCellComponent,
      sortable: false,
      width: 200
    },
    {
      name: TeamColumn.FALSE_NAMES,
      label: 'False names',
      type: SplitCellComponent,
      sortable: true,
      width: 200
    }];
}
