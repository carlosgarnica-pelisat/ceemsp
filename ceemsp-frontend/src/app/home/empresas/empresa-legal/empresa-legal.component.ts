import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-empresa-legal',
  templateUrl: './empresa-legal.component.html',
  styleUrls: ['./empresa-legal.component.css']
})
export class EmpresaLegalComponent implements OnInit {

  uuid: string;

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");
  }

  mostrarModalCrear(modal) {

  }

}
