import {CellComponent} from '../../cell';
import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-grid-button-cell',
  templateUrl: './button-cell.component.html',
  styleUrls: ['./button-cell.component.scss']
})
export class ButtonCellComponent extends CellComponent implements OnInit {

  public static readonly parameterNames = {
    BUTTON_ACTIONS: 'buttonActions',
    BUTTON_PARAMS: 'buttonParams',
    BUTTON_TYPES: 'buttonTypes',
    ICON_NAMES: 'iconNames',
    DISABLED: 'disabled',
    TOOLTIP: 'tooltip'
  };

  public static readonly buttonType = {
    MAT_BUTTON: 'matButton',
    MAT_RAISED_BUTTON: 'matRaisedButton',
    MAT_TABLE_BUTTON: 'matTableButton',
    MAT_ICON: 'matIcon'
  };

  public buttonType: string;
  public iconName: string;

  constructor() {
    super();
  }

  ngOnInit() {
    if (
      this.row[ButtonCellComponent.parameterNames.BUTTON_TYPES] &&
      this.row[ButtonCellComponent.parameterNames.BUTTON_TYPES][this.columnName]
    ) {
      this.buttonType = this.row[ButtonCellComponent.parameterNames.BUTTON_TYPES][this.columnName];
    } else {
      this.buttonType = ButtonCellComponent.buttonType.MAT_RAISED_BUTTON;
    }

    if (this.buttonType === ButtonCellComponent.buttonType.MAT_ICON) {
      this.iconName = this.row[ButtonCellComponent.parameterNames.ICON_NAMES][this.columnName];
    }
  }
}
