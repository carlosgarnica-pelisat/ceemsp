import { Component, OnInit } from '@angular/core';
import {ICellRendererAngularComp} from "ag-grid-angular";
import {ICellRendererParams} from "ag-grid-community";

@Component({
  selector: 'app-multiline-cell',
  template: `
    <p style="word-break: break-word; inline-size: 30px; ">Puto: {{ value }}</p>
  `
})
export class MultilineCellComponent implements ICellRendererAngularComp, OnInit {

  value: any;
  agInit(params: ICellRendererParams): void {
    this.value = params.value
  }

  refresh(params: ICellRendererParams): boolean {
    return false;
  }

  ngOnInit(): void {
  }

}
