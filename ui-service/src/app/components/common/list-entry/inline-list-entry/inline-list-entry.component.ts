import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-inline-list-entry',
  templateUrl: './inline-list-entry.component.html',
  styleUrls: ['./inline-list-entry.component.scss']
})
export class InlineListEntryComponent {

  @Input()
  label: string;
  @Input()
  data: any;
  @Input()
  link?: string;
  @Input()
  active ? = true;
  @Input()
  newTab ? = false;
  @Input()
  marked ? = false;

  constructor() {
  }
}


