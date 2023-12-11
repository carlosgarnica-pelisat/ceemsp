import {Deserializable} from "./Deserializable";
import Incidencia from "./Incidencia";
import Empresa from "./Empresa";
import Cliente from "./Cliente";
import Persona from "./Persona";
import Can from "./Can";
import Vehiculo from "./Vehiculo";
import Arma from "./Arma";
import EmpresaEscritura from "./EmpresaEscritura";

export default class ResultadosBusqueda implements Deserializable {
  palabraABuscar: string;
  filtro: string;
  resultadosBusquedaIncidencias: Incidencia[];
  resultadosBusquedaEmpresas: Empresa[];
  resultadosBusquedaClientes: Cliente[];
  resultadosBusquedaPersona: Persona[];
  resultadosBusquedaCan: Can[];
  resultadosBusquedaVehiculo: Vehiculo[];
  resultadosBusquedaArma: Arma[];
  resultadosBusquedaEscritura: EmpresaEscritura[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
