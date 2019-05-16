import {Component, OnInit} from '@angular/core';
import {CellComponent} from '../../cell';

@Component({
  selector: 'app-styled-cell',
  templateUrl: './styled-cell.component.html',
  styleUrls: ['./styled-cell.component.scss']
})
export class StyledCellComponent extends CellComponent implements OnInit {

  public static readonly parameterNames = {
    CLASSES: 'styleCellClasses'
  };

  public cssClass: string;

  constructor() {
    super();

  }

  ngOnInit() {
    if (this.row[StyledCellComponent.parameterNames.CLASSES]) {
      this.cssClass = this.row[StyledCellComponent.parameterNames.CLASSES][this.columnName];
    }
  }

}
