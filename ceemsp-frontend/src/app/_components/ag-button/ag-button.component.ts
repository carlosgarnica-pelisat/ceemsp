import { Component, OnInit } from '@angular/core';
import { ICellRendererAngularComp } from "ag-grid-angular";
import { ICellRendererParams } from "ag-grid-community";
import { faTrash, faPencilAlt } from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-ag-button',
  template: `
    <a style="cursor: pointer;"><fa-icon [icon]="faPencilAlt" class="fas fa-lg text-primary" (click)="evaluateCallback($event, this.MODIFY)"></fa-icon></a>
    <a style="cursor: pointer;"><fa-icon [icon]="faTrash" class="fas fa-lg text-primary" style="margin-left: 10px;" (click)="evaluateCallback($event, this.DELETE)"></fa-icon></a>
  `
})
export class AgButtonComponent implements ICellRendererAngularComp {

  MODIFY: string = "MODIFY";
  DELETE: string = "DELETE";

  params;
  faPencilAlt = faPencilAlt;
  faTrash = faTrash;

  constructor() { }

  agInit(params: ICellRendererParams): void {
    this.params = params;
  }

  refresh(params: ICellRendererParams): boolean {
    return true;
  }

  evaluateCallback($event, callbackType: string) {
    const params = {
      event: $event,
      rowData: this.params.node.data
    }

    switch(callbackType) {
      case this.MODIFY:
        this.params.modify(params);
        break;
      case this.DELETE:
        this.params.delete(params);
        break;
    }
  }

}
