import {Component} from '@angular/core';
import {CellComponent} from '../../cell';

@Component({
  selector: 'app-action-link-cell',
  templateUrl: './action-link-cell.component.html',
  styleUrls: ['./action-link-cell.component.scss']
})
export class ActionLinkCellComponent extends CellComponent {

  public static readonly ParameterNames = {
    LINK_ACTIONS: 'linkActions',
    LINK_PARAMS: 'linkParams'
  };

  constructor() {
    super();

  }
}
