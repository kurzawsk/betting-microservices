import {MultiColorCellComponent, RouterLinkCellComponent} from '../common/ui-grid/cell';
import {Match, ResultType} from './model/data-model';
import {MatchColumn} from './model/match-column-model';
import {Constants} from '../common/Constants';
import {DateCellComponent} from '../common/ui-grid/cells/date-cell/date-cell.component';

export class MatchTeamLogic {

  public static readonly RESULT_TYPES = [
    {id: ResultType.NORMAL, label: 'Normal'},
    {id: ResultType.CANCELLED, label: 'Cancelled'},
    {id: ResultType.NOT_FOUND, label: 'Not found'},
    {id: ResultType.POSTPONED, label: 'Postponed'},
    {id: ResultType.UNKNOWN, label: 'Unknown'}
  ];

  public static prepareMatchData = (rawData: Match[]): Match[] => {
    const gridRows = [];
    for (const entry of rawData) {
      const additionalRowProps = {};

      additionalRowProps[RouterLinkCellComponent.parameterNames.ROUTER_LINKS] = {};
      additionalRowProps[RouterLinkCellComponent.parameterNames.ROUTER_LINKS][MatchColumn.ID] = '/match/' + entry.id;

      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT] = {};
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT][MatchColumn.START_TIME] = Constants.DATE_TIME_FORMAT;
      additionalRowProps[DateCellComponent.parameterNames.DATE_FORMAT][MatchColumn.MARKED_AS_FINISHED_TIME] = Constants.DATE_TIME_FORMAT;
      additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS] = {};
      let resultTypeColorClass = 'black';
      switch (entry.resultType) {
        case ResultType.NORMAL:
          resultTypeColorClass = 'green';
          break;
        case ResultType.UNKNOWN:
          resultTypeColorClass = 'grey';
          break;
        case ResultType.CANCELLED:
          resultTypeColorClass = 'light-orange';
          break;
        case ResultType.POSTPONED:
          resultTypeColorClass = 'dark-orange';
          break;
        case ResultType.NOT_FOUND:
          resultTypeColorClass = 'red';
          break;
      }
      additionalRowProps[MultiColorCellComponent.parameterNames.MULTICOLOR_FIELDS][MatchColumn.RESULT_TYPE_LBL] = resultTypeColorClass;
      additionalRowProps[MatchColumn.RESULT_TYPE_LBL] = MatchTeamLogic.getResultTypeLabel(entry.resultType);

      additionalRowProps[MatchColumn.HOME_TEAM_NAME] = entry.homeTeam.name;
      additionalRowProps[MatchColumn.AWAY_TEAM_NAME] = entry.awayTeam.name;
      additionalRowProps[RouterLinkCellComponent.parameterNames.ROUTER_LINKS][MatchColumn.HOME_TEAM_NAME] = '/team/' + entry.homeTeam.id;
      additionalRowProps[RouterLinkCellComponent.parameterNames.ROUTER_LINKS][MatchColumn.AWAY_TEAM_NAME] = '/team/' + entry.awayTeam.id;
      if (entry.resultType === 'NORMAL') {
        additionalRowProps[MatchColumn.RESULT] = entry.homeScore + ' - ' + entry.awayScore;
      }

      gridRows.push(Object.assign(entry, additionalRowProps));
    }
    return gridRows;
  }

  public static getResultTypeLabel(id) {
    return MatchTeamLogic.RESULT_TYPES.find(rt => rt.id === id).label;
  }
}
