import {GridColumn} from '../../common/ui-grid/grid-definitions';
import {MultiColorCellComponent, RouterLinkCellComponent, TextCellComponent} from '../../common/ui-grid/cell';
import {MappingCaseColumn} from '../model/mapping-case-column-model';
import {DateCellComponent} from '../../common/ui-grid/cells/date-cell/date-cell.component';
import {ButtonCellComponent} from '../../common/ui-grid/cells/button-cell/button-cell.component';

export class ColumnConfig {
  public static readonly MAPPING_CASE_COLUMN_CONFIG: GridColumn[] = [
    {
      name: MappingCaseColumn.ID,
      label: 'Id',
      type: TextCellComponent,
      sortable: true,
      width: 90
    },
    {
      name: MappingCaseColumn.HOME_TEAM_NAME,
      label: 'Home Team Name',
      type: TextCellComponent,
      sortable: false,
      width: 170
    },
    {
      name: MappingCaseColumn.AWAY_TEAM_NAME,
      label: 'Away Team Name',
      type: TextCellComponent,
      sortable: false,
      width: 170
    },
    {
      name: MappingCaseColumn.MATCH_TEAMS,
      label: 'Match',
      type: RouterLinkCellComponent,
      sortable: true,
      width: 180
    },
    {
      name: MappingCaseColumn.HOME_SIMILARITY_FACTOR,
      label: 'Home Team Sim. Factor',
      type: TextCellComponent,
      sortable: true,
      width: 90
    },

    {
      name: MappingCaseColumn.AWAY_SIMILARITY_FACTOR,
      label: 'Away Team Sim. Factor',
      type: TextCellComponent,
      sortable: true,
      width: 90
    },
    {
      name: MappingCaseColumn.SOURCE_SYSTEM_NAME,
      label: 'Source System Name',
      type: TextCellComponent,
      sortable: true,
      width: 70
    },

    {
      name: MappingCaseColumn.CREATED_ON,
      label: 'Created On',
      type: DateCellComponent,
      sortable: true,
      width: 140
    },
    {
      name: MappingCaseColumn.CREATED_BY,
      label: 'Created By',
      type: TextCellComponent,
      sortable: true,
      width: 80
    },
    {
      name: MappingCaseColumn.UPDATED_ON,
      label: 'Updated On',
      type: DateCellComponent,
      sortable: true,
      width: 140
    },
    {
      name: MappingCaseColumn.UPDATED_BY,
      label: 'Updated By',
      type: TextCellComponent,
      sortable: true,
      width: 80
    },
    {
      name: MappingCaseColumn.STATUS,
      label: 'Status',
      type: MultiColorCellComponent,
      sortable: true,
      width: 80
    },
    {
      name: MappingCaseColumn.ACCEPT,
      label: 'Accept',
      type: ButtonCellComponent,
      width: 50
    },
    {
      name: MappingCaseColumn.REJECT,
      label: 'Reject',
      type: ButtonCellComponent,
      width: 50
    }

  ];
}
