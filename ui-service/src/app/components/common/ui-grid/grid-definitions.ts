import {Type} from '@angular/core';
import {
  CellComponent, ExpandableTextCellComponent, HtmlCellComponent, MultiColorCellComponent, RouterLinkCellComponent,
  TextCellComponent
} from './cell';
import {ButtonCellComponent} from './cells/button-cell/button-cell.component';

export class GridDefinitions {

  public columns: GridColumn[];
  public names: string[];
  public labels: string[];

  constructor(columns: GridColumn[]) {
    this.columns = columns;
    this.names = this.extractColumnNames(columns);
    this.labels = this.extractColumnLabels(columns);
  }

  private extractColumnNames(colDefs: GridColumn[]): string[] {
    const columnNames = [];
    for (const column of colDefs) {
      columnNames.push(column.name);
    }
    return columnNames;
  }

  private extractColumnLabels(colDefs: GridColumn[]): string[] {
    const columnLabels = [];
    for (const column of colDefs) {
      columnLabels.push(column.label);
    }
    return columnLabels;
  }
}

export enum CellTypeName {
  CellComponent,
  TextCellComponent,
  ExpandableTextCellComponent,
  HtmlCellComponent,
  RouterLinkCellComponent,
  ButtonCellComponent,
  MultiColorCellComponent,
  CheckboxCellComponent
}

export class GridSubType {
  public name: string;
  public label: string;
  public checked?: boolean;
  public isInput?: boolean;
  public textValue?: string;

  constructor(name: string, label: string, checked?: boolean, isInput?: boolean) {
    this.name = name;
    this.label = label;
    this.checked = checked !== undefined ? checked : false;
    this.isInput = isInput !== undefined ? isInput : false;
    this.textValue = '';
  }
}

export class GridGroup {
  public col: number;
  public row: number;

  constructor(col: number, row: number) {
    this.col = col;
    this.row = row;
  }

}

export class GridColumn {
  public name: string;
  public label: string;
  public type?: Type<CellComponent>;
  public typeName?: CellTypeName;
  public sortable?: boolean;
  public group?: GridGroup;
  public checked?: boolean;
  public width?: number;
  public selection?: boolean;
}
