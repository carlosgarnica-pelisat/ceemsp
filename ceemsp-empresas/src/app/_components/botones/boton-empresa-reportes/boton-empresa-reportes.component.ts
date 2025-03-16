import { Component, OnInit } from '@angular/core';
import {faEye} from "@fortawesome/free-solid-svg-icons";
import {ICellRendererParams} from "ag-grid-community";
import {ICellRendererAngularComp} from "ag-grid-angular";

@Component({
  selector: 'app-boton-empresa-reportes',
  template: `
    <fa-icon [icon]="faEye" size="fa-lg" style="cursor: pointer;" (click)="evaluateCallback($event, this.VER_DETALLES)"></fa-icon>
  `
})
export class BotonEmpresaReportesComponent implements ICellRendererAngularComp {
  params;
  label: string;
  faEye = faEye;
  VER_DETALLES: string = "VER_DETALLES";

  agInit(params: ICellRendererParams): void {
    this.params = params;
    this.label = this.params.label || null;
  }

  refresh(params: ICellRendererParams): boolean {
    return true;
  }

  evaluateCallback($event, callbackType: string) {
    const params = {
      event: $event,
      rowData: this.params.node.data
    }

    switch (callbackType) {
      case this.VER_DETALLES:
        this.params.verDetalles(params);
        break;
    }
  }

}
