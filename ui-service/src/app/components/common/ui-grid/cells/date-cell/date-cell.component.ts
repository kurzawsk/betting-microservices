import {Component, OnInit} from '@angular/core';
import {CellComponent} from '../../cell';

@Component({
  selector: 'app-date-cell',
  templateUrl: './date-cell.component.html',
  styleUrls: ['./date-cell.component.scss']
})
export class DateCellComponent extends CellComponent implements OnInit {

  public static readonly parameterNames = {
    DATE_FORMAT: 'dateFormat',
    DATE_TIMEZONE: 'dateTimezone'
  };

  dateFormat ? = 'mediumDate';
  dateTimeZone?: string;

  constructor() {
    super();

  }

  ngOnInit() {
    if (this.row[DateCellComponent.parameterNames.DATE_FORMAT] &&
      this.row[DateCellComponent.parameterNames.DATE_FORMAT][this.columnName]) {
      this.dateFormat = this.row[DateCellComponent.parameterNames.DATE_FORMAT][this.columnName];
    }

    if (this.row[DateCellComponent.parameterNames.DATE_TIMEZONE] &&
      this.row[DateCellComponent.parameterNames.DATE_TIMEZONE][this.columnName]) {
      this.dateTimeZone = this.row[DateCellComponent.parameterNames.DATE_TIMEZONE][this.columnName];
    }
  }
}
