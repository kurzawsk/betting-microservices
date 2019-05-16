import {Component, OnInit, Type} from '@angular/core';
import {CellComponent} from '../../cell';

@Component({
  selector: 'app-varied-type-cell',
  templateUrl: './varied-type-cell.component.html',
  styleUrls: ['./varied-type-cell.component.scss']
})
export class VariedTypeCellComponent extends CellComponent implements OnInit {

  public static readonly parameterNames = {
    CELL_TYPE: 'VARIED_TYPE_CELL_CELL_TYPES'
  };

  public cellType: Type<CellComponent>;

  constructor() {
    super();
  }

  ngOnInit() {
    if (this.row[VariedTypeCellComponent.parameterNames.CELL_TYPE]
      && this.row[VariedTypeCellComponent.parameterNames.CELL_TYPE][this.columnName]) {
      this.cellType = this.row[VariedTypeCellComponent.parameterNames.CELL_TYPE][this.columnName];
    }
  }

}
