import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {BusquedaService} from "../../_services/busqueda.service";
import ResultadosBusqueda from "../../_models/ResultadosBusqueda";
import Incidencia from "../../_models/Incidencia";
import Empresa from "../../_models/Empresa";
import Cliente from "../../_models/Cliente";
import Persona from "../../_models/Persona";
import Vehiculo from "../../_models/Vehiculo";
import Can from "../../_models/Can";
import Arma from "../../_models/Arma";
import {faEye} from "@fortawesome/free-solid-svg-icons";
import EmpresaEscritura from "../../_models/EmpresaEscritura";

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
  resultadoEmpresas: Empresa[];
  resultadoClientes: Cliente[];
  resultadoPersonas: Persona[];
  resultadoCanes: Can[];
  resultadoVehiculos: Vehiculo[];
  resultadoArmas: Arma[];
  resultadoEscrituras: EmpresaEscritura[];

  constructor(private router: Router, private busquedaService: BusquedaService) {}

  ngOnInit(): void {
    this.resultadoBusqueda = this.busquedaService.obtenerResultadosBusqueda();
    switch(this.resultadoBusqueda.filtro) {
      case 'EMPRESAS':
        this.resultadoEmpresas = this.resultadoBusqueda.resultadosBusquedaEmpresas;
        break;
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

  verDetallesEmpresa(uuid: string) {
    this.router.navigate([`/home/empresas/${uuid}`])
  }

  verDetalles(tipoFiltro: string, uuidEmpresa: string, uuid: string) {
    let nextRoute: string = "";
    switch(tipoFiltro) {
      case 'CLIENTES':
        nextRoute = `/home/empresas/${uuidEmpresa}/clientes`
        break;
      case 'PERSONAL':
        nextRoute = `/home/empresas/${uuidEmpresa}/personal`
        break;
      case 'CANES':
        nextRoute = `/home/empresas/${uuidEmpresa}/canes`
        break;
      case 'VEHICULOS':
        nextRoute = `/home/empresas/${uuidEmpresa}/vehiculos`
        break;
      case 'ARMAS':
        nextRoute = `/home/empresas/${uuidEmpresa}/armas`
        break;
      case 'ESCRITURAS':
        nextRoute = `/home/empresas/${uuidEmpresa}/escrituras`
        break;
      case 'INCIDENCIAS':
        nextRoute = `/home/empresas/${uuidEmpresa}/incidencias`
        break;
    }

    this.router.navigate([nextRoute], {queryParams: {uuid: uuid}})
  }

}
