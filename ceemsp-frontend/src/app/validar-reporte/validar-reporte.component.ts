import { Component, OnInit } from '@angular/core';
import {PublicService} from "../_services/public.service";
import {ActivatedRoute} from "@angular/router";
import ValidarAcuse from "../_models/ValidarAcuse";
import ValidarReporte from "../_models/ValidarReporte";

@Component({
  selector: 'app-validar-reporte',
  templateUrl: './validar-reporte.component.html',
  styleUrls: ['./validar-reporte.component.css']
})
export class ValidarReporteComponent implements OnInit {
  datosInvalidos: boolean;
  informacionEncontrada: boolean;
  informacionNoEncontrada: boolean;
  validarReporte: ValidarReporte;

  constructor(private publicService: PublicService, private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      if(params.sello === '' || params.sello === undefined) {
        this.datosInvalidos = true;
        return;
      }

      this.publicService.validarAcuseInformeMensual(params.sello).subscribe((data: ValidarReporte) => {
        this.informacionEncontrada = true;
        this.validarReporte = data;
      }, (error) => {
        this.informacionNoEncontrada = true;
      })
    })
  }

}
