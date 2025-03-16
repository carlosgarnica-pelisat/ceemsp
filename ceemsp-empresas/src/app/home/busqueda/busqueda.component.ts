import { Component, OnInit } from '@angular/core';
import ResultadosBusqueda from "../../_models/ResultadosBusqueda";
import Incidencia from "../../_models/Incidencia";
import Empresa from "../../_models/Empresa";
import Cliente from "../../_models/Cliente";
import Persona from "../../_models/Persona";
import Can from "../../_models/Can";
import Vehiculo from "../../_models/Vehiculo";
import Arma from "../../_models/Arma";
import EmpresaEscritura from "../../_models/EmpresaEscritura";
import {Router} from "@angular/router";
import {BusquedaService} from "../../_services/busqueda.service";
import {faEye} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-busqueda',
  templateUrl: './busqueda.component.html',
  styleUrls: ['./busqueda.component.css']
})
export class BusquedaComponent implements OnInit {

  faEye = faEye;

  resultadoBusqueda: ResultadosBusqueda;

  // Resultados en incidencias
  resultadoIncidencias: Incidencia[];
  resultadoClientes: Cliente[];
  resultadoPersonas: Persona[];
  resultadoCanes: Can[];
  resultadoVehiculos: Vehiculo[];
  resultadoArmas: Arma[];
  resultadoEscrituras: EmpresaEscritura[];

  constructor(private router: Router, private busquedaService: BusquedaService) { }

  ngOnInit(): void {
    this.resultadoBusqueda = this.busquedaService.obtenerResultadosBusqueda();
    switch(this.resultadoBusqueda.filtro) {
      case 'CLIENTES':
        this.resultadoClientes = this.resultadoBusqueda.resultadosBusquedaClientes;
        break;
      case 'PERSONAL':
        this.resultadoPersonas = this.resultadoBusqueda.resultadosBusquedaPersona;
        break;
      case 'CANES':
        this.resultadoCanes = this.resultadoBusqueda.resultadosBusquedaCan;
        break;
      case 'VEHICULOS':
        this.resultadoVehiculos = this.resultadoBusqueda.resultadosBusquedaVehiculo;
        break;
      case 'ARMAS':
        this.resultadoArmas = this.resultadoBusqueda.resultadosBusquedaArma;
        break;
      case 'ESCRITURAS':
        this.resultadoEscrituras = this.resultadoBusqueda.resultadosBusquedaEscritura;
        break;
      case 'INCIDENCIAS':
        this.resultadoIncidencias = this.resultadoBusqueda.resultadosBusquedaIncidencias;
        break;
    }
  }

  verDetalles(tipoFiltro: string, uuid: string) {
    let nextRoute: string = "";
    switch(tipoFiltro) {
      case 'CLIENTES':
        nextRoute = `/home/clientes`
        break;
      case 'PERSONAL':
        nextRoute = `/home/personal`
        break;
      case 'CANES':
        nextRoute = `/home/canes`
        break;
      case 'VEHICULOS':
        nextRoute = `/home/vehiculos`
        break;
      case 'ARMAS':
        nextRoute = `/home/armas`
        break;
      case 'ESCRITURAS':
        nextRoute = `/home/escrituras`
        break;
      case 'INCIDENCIAS':
        nextRoute = `/home/incidencias`
        break;
    }

    this.router.navigate([nextRoute], {queryParams: {uuid: uuid}})
  }

}
