import {Component, OnInit} from '@angular/core';
import {CellComponent} from '../../cell';

@Component({
  selector: 'app-split-cell',
  templateUrl: './split-cell.component.html',
  styleUrls: ['./split-cell.component.scss']
})
export class SplitCellComponent extends CellComponent implements OnInit {

  public static readonly parameterNames = {
    DIVIDED: 'divided'
  };

  constructor() {
    super();
  }

  ngOnInit() {
  }

  public isArray(obj: any) {
    return Array.isArray(obj);
  }

}
