import {
  AfterViewInit, ChangeDetectorRef, Component, ComponentFactoryResolver, ComponentRef, ElementRef, Inject, Input,
  OnChanges, OnDestroy, OnInit, Type, ViewChild, ViewContainerRef
} from '@angular/core';
import {SelectionModel} from '@angular/cdk/collections';
import {Subject} from 'rxjs/index';
import {MAT_DIALOG_DATA, MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material';

@Component({
  selector: 'app-cell',
  template: '<div #cell></div>',
})
export class CellContainerComponent implements OnChanges, AfterViewInit, OnDestroy {
  @ViewChild('cell', {read: ViewContainerRef}) private cell: ViewContainerRef;
  @Input() private type: Type<CellComponent>;
  @Input() private row: {}[];
  @Input() private columnName: string;
  @Input() private selection: SelectionModel<any>;
  private cellComponentRef: ComponentRef<CellComponent>;
  private isViewInitialized = false;

  constructor(private componentFactoryResolver: ComponentFactoryResolver, private changeDetector: ChangeDetectorRef) {
  }

  updateComponent() {
    if (this.isViewInitialized) {
      if (this.cellComponentRef) {
        this.cellComponentRef.destroy();
      }

      const factory = this.componentFactoryResolver.resolveComponentFactory(this.type);
      this.cellComponentRef = this.cell.createComponent(factory);
      this.cellComponentRef.instance['row'] = this.row;
      this.cellComponentRef.instance['columnName'] = this.columnName;
      this.cellComponentRef.instance['selection'] = this.selection;
      this.changeDetector.detectChanges();
    }

  }

  ngOnChanges() {
    this.updateComponent();
  }

  ngAfterViewInit() {
    this.isViewInitialized = true;
    this.updateComponent();
  }

  ngOnDestroy() {
    if (this.cellComponentRef) {
      this.cellComponentRef.destroy();
    }
  }
}

@Component({
  selector: 'app-empty-cell',
  template: ''
})
export class CellComponent {
  row: {};
  columnName: string;
  selection: SelectionModel<any>;
}

@Component({
  selector: 'app-grid-text-cell',
  template: '<div>{{row[columnName]}}</div>',
})
export class TextCellComponent extends CellComponent {
  constructor() {
    super();
  }
}

@Component({
  selector: 'app-simple-text-dialog-component',
  template: '<mat-dialog-content><span style="white-space: pre-wrap">{{data.text}}</span></mat-dialog-content>\n' +
  '<mat-dialog-actions align="end">\n' +
  '  <button  mat-button [mat-dialog-close]="true">Close</button>\n' +
  '</mat-dialog-actions>'
})
export class SimpleTextDialogComponent {

  constructor(@Inject(MAT_DIALOG_DATA) public data, public dialogRef: MatDialogRef<SimpleTextDialogComponent>) {
  }

}

@Component({
  selector: 'app-grid-expandable-text-cell',
  template: '<div style="  width: 100%;' +
  '    white-space: nowrap;' +
  '    overflow: hidden;' +
  '    text-overflow: ellipsis;" (dblclick)="expand()">{{row[columnName]}}</div>',
})
export class ExpandableTextCellComponent extends CellComponent {
  constructor(private matDialog: MatDialog) {
    super();
  }

  public expand() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.maxWidth = '50%';
    dialogConfig.closeOnNavigation = true;
    dialogConfig.minHeight = 100;
    dialogConfig.maxHeight = 800;
    dialogConfig.data = {text: this.row[this.columnName]};
    this.matDialog.open(SimpleTextDialogComponent, dialogConfig);
  }
}

@Component({
  selector: 'app-grid-text-cell',
  template: '<div [innerHtml]="row[columnName]"></div>',
})
export class HtmlCellComponent extends CellComponent {

  constructor() {
    super();
  }
}

@Component({
  selector: 'app-grid-checkbox-cell',
  template: '<mat-checkbox (click)="$event.stopPropagation()" ' +
  '(change)="$event ? selection.toggle(row) : null" [checked]="selection.isSelected(row)"' +
  '[disabled]="row[columnName]"></mat-checkbox>'
})
export class CheckboxCellComponent extends CellComponent {

  constructor(public elem: ElementRef) {
    super();
    elem.nativeElement.component = this;
  }
}

@Component({
  selector: 'app-grid-router-link-cell',
  template: '<a *ngIf="row[\'routerLinks\'] && row[\'routerLinks\'][columnName]" ' +
  'class="link" [routerLink]="row[\'routerLinks\'][columnName]" ' +
  '(mouseenter)="enterHandler($event)" (mouseleave)="leaveHandler($event)" ' +
  'target="{{newTab ? \'_blank\':\'_self\'}}" ' +
  '>{{row[columnName]}}</a><div #cmtTooltip class="case-tooltip" (mouseenter)="enterTooltip()" (mouseleave)="leaveTooltip()"></div>',
  styleUrls: ['./grid.component.scss'],
})
export class RouterLinkCellComponent extends CellComponent implements OnInit {
  public static readonly parameterNames = {
    ROUTER_LINKS: 'routerLinks',
    TOOLTIP_HANDLER: 'tooltipHandler',
    OPEN_IN_NEW_TAB: 'openInNewTab'
  };
  @ViewChild('tooltip') popup: ElementRef;
  public newTab = false;
  private tooltipDiv;
  private linkHandler;
  private hideTooltip = new Subject<boolean>();
  private overTooltip = false;

  constructor() {
    super();
  }

  ngOnInit(): void {
    if (this.row[RouterLinkCellComponent.parameterNames.OPEN_IN_NEW_TAB]
      && this.row[RouterLinkCellComponent.parameterNames.OPEN_IN_NEW_TAB][this.columnName]) {
      this.newTab = true;
    }
    if (!this.row[RouterLinkCellComponent.parameterNames.TOOLTIP_HANDLER]) {
      return;
    }
    this.linkHandler = this.row[RouterLinkCellComponent.parameterNames.TOOLTIP_HANDLER][this.columnName];
    this.hideTooltip.subscribe(trueValue => {
      if (trueValue && this.tooltipDiv && !this.overTooltip) {
        this.tooltipDiv.querySelector('div').style.display = 'none';
      }
    });
  }

  enterHandler(event) {
    if (!this.linkHandler) {
      return;
    }
    const caseId = this.row['id'];
    // this.linkHandler.getLightPartyDetails(caseId.toString()).subscribe(response => {
    //   this.tooltipDiv = this.popup.nativeElement;
    //   if (!this.tooltipDiv) {
    //     return;
    //   }
    //   this.tooltipDiv.innerHTML = <string> response;
    //   const caseTooltip = this.tooltipDiv.querySelector('div');
    //   caseTooltip.style.display = 'block';
    //   caseTooltip.style.padding = '10px 20px 10px 10px';
    //   caseTooltip.style.top = event.pageY;
    //   caseTooltip.style.left = event.pageX;
    //   const table = caseTooltip.querySelector('table');
    //   table.style.borderCollapse = 'collapse';
    //   table.style.borderSpacing = '0';
    //   table.style.textAlign = 'left';
    //   for (const first of table.querySelectorAll('td:first-child')) {
    //     first.style.borderRight = '1px solid #E0E0E0';
    //   }
    //   for (const td of table.querySelectorAll('td')) {
    //     td.style.borderBottom = '1px solid #E0E0E0';
    //     td.style.borderTop = '1px solid #E0E0E0';
    //     td.style.padding = '4px 8px';
    //   }
    // });
  }

  leaveHandler(event) {
    if (!this.linkHandler) {
      return;
    }

    const hide = () => {
      this.hideTooltip.next(true);
    };
    setTimeout(hide, 500);
  }

  enterTooltip() {
    this.overTooltip = true;
  }

  leaveTooltip() {
    this.overTooltip = false;
    this.hideTooltip.next(true);
  }
}

@Component({
  selector: 'app-grid-multicolor-cell',
  template: '<p *ngIf="row[\'multicolorFields\'] && row[\'multicolorFields\'][columnName]" ' +
  ' [ngClass]="row[\'multicolorFields\'][columnName]">{{row[columnName]}}</p>',
})
export class MultiColorCellComponent extends CellComponent {
  public static readonly parameterNames = {
    MULTICOLOR_FIELDS: 'multicolorFields'
  };

  constructor() {
    super();
  }
}


