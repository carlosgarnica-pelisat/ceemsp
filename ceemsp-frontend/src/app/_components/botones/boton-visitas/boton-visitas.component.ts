import { Component } from '@angular/core';
import {faEye, faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import {ICellRendererParams} from "ag-grid-community";
import {ICellRendererAngularComp} from "ag-grid-angular";

@Component({
  selector: 'app-boton-visitas',
  template: `
    <fa-icon [icon]="faEye" size="fa-lg" style="cursor: pointer;" (click)="evaluateCallback($event, this.VER_DETALLES)"></fa-icon>
    <fa-icon [icon]="faPencil" size="fa-lg" style="cursor: pointer;" (click)="evaluateCallback($event, this.EDITAR)"></fa-icon>
    <fa-icon [icon]="faTrash" size="fa-lg" style="cursor: pointer;" (click)="evaluateCallback($event, this.ELIMINAR)"></fa-icon>
  `
})
export class BotonVisitasComponent implements ICellRendererAngularComp {
  params;
  label: string;

  faPencil = faPencilAlt;
  faTrash = faTrash;
  faEye = faEye;

  VER_DETALLES: string = "VER_DETALLES";
  EDITAR: string = "EDITAR";
  ELIMINAR: string = "ELIMINAR";

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
        console.log("Viendo detalles");
        this.params.verDetalles(params);
        break;
      case this.EDITAR:
        this.params.editar(params);
        break;
      case this.ELIMINAR:
        this.params.eliminar(params);
        break;
    }
  }

}
