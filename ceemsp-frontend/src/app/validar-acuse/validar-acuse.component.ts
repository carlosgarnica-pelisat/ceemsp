import { Component, OnInit } from '@angular/core';
import {PublicService} from "../_services/public.service";
import ValidarAcuse from "../_models/ValidarAcuse";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-validar-acuse',
  templateUrl: './validar-acuse.component.html',
  styleUrls: ['./validar-acuse.component.css']
})
export class ValidarAcuseComponent implements OnInit {
  datosInvalidos: boolean;
  informacionEncontrada: boolean;
  informacionNoEncontrada: boolean;
  validarAcuse: ValidarAcuse;

  constructor(private publicService: PublicService, private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      if(params.sello === '' || params.sello === undefined) {
        this.datosInvalidos = true;
        return;
      }

      this.publicService.validarAcuseSello(params.sello).subscribe((data: ValidarAcuse) => {
        this.informacionEncontrada = true;
        this.validarAcuse = data;
      }, (error) => {
        this.informacionNoEncontrada = true;
      })
    })
  }

}
