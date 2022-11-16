import { Component } from '@angular/core';
import {ICellRendererAngularComp} from "ag-grid-angular";
import {faUser, faSync, faEye} from "@fortawesome/free-solid-svg-icons";
import {ICellRendererParams} from "ag-grid-community";

@Component({
  selector: 'app-boton-empresa-incidencias',
  template: `
    <fa-icon [icon]="faEye" size="fa-lg" style="cursor: pointer;" (click)="evaluateCallback($event, this.VER_DETALLES)"></fa-icon>
    <fa-icon [icon]="faUser" size="fa-lg" style="cursor: pointer;" (click)="evaluateCallback($event, this.CAMBIAR_ASIGNADO)"></fa-icon>
    <fa-icon [icon]="faSync" size="fa-lg" style="cursor: pointer;" (click)="evaluateCallback($event, this.CAMBIAR_STATUS)"></fa-icon>
  `
})
export class BotonEmpresaIncidenciasComponent implements ICellRendererAngularComp {

  params;
  label: string;

  faUser = faUser;
  faSync = faSync;
  faEye = faEye;

  VER_DETALLES: string = "VER_DETALLES";
  CAMBIAR_ASIGNADO: string = "CAMBIAR_ASIGNADO";
  CAMBIAR_STATUS: string = "CAMBIAR_STATUS";

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
      case this.CAMBIAR_ASIGNADO:
        this.params.cambiarAsignado(params);
        break;
      case this.CAMBIAR_STATUS:
        this.params.cambiarStatus(params);
    }
  }
}
