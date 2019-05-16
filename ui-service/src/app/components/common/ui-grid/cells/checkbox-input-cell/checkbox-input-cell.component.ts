import {Component, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {CellComponent} from '../../cell';

@Component({
  selector: 'app-checkbox-input-cell',
  templateUrl: './checkbox-input-cell.component.html',
  styleUrls: ['./checkbox-input-cell.component.scss']
})
export class CheckboxInputCellComponent extends CellComponent implements OnInit {

  public static readonly parameterNames = {
    FORM_CONTROL_NAMES: 'CHECKBOX_INPUT_CELL_FORM_CONTROL_NAMES',
    FORM_GROUPS: 'CHECKBOX_INPUT_CELL_FORM_GROUPS',
    IS_DISABLED: 'CHECKBOX_INPUT_CELL_IS_DISABLED'
  };

  @ViewChild('checkbox')
  checkbox;

  form: FormGroup;
  disabled = false;
  formControlName: string;

  constructor() {
    super();
  }

  ngOnInit() {
    if (this.row[CheckboxInputCellComponent.parameterNames.FORM_GROUPS]
      && this.row[CheckboxInputCellComponent.parameterNames.FORM_GROUPS][this.columnName]
      && this.row[CheckboxInputCellComponent.parameterNames.FORM_CONTROL_NAMES]
      && this.row[CheckboxInputCellComponent.parameterNames.FORM_CONTROL_NAMES][this.columnName]) {
      this.form = this.row[CheckboxInputCellComponent.parameterNames.FORM_GROUPS][this.columnName];
      this.formControlName = this.row[CheckboxInputCellComponent.parameterNames.FORM_CONTROL_NAMES][this.columnName];
      this.form.addControl(this.formControlName, new FormControl({
        value: this.row[this.columnName],
        disabled: this.row[CheckboxInputCellComponent.parameterNames.IS_DISABLED][this.columnName]
      }));

    }
  }

}
