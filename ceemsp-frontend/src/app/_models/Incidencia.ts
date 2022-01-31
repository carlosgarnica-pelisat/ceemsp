import {Deserializable} from "./Deserializable";
import Cliente from "./Cliente";
import Usuario from "./Usuario";
import Can from "./Can";
import Arma from "./Arma";
import IncidenciaComentario from "./IncidenciaComentario";
import Persona from "./Persona";
import Vehiculo from "./Vehiculo";

export default class Incidencia implements Deserializable {
  id: number;
  uuid: string;
  numero: string;
  fechaIncidencia: string;
  cliente: Cliente;
  status: string;
  asignado: Usuario;

  canesInvolucrados: Can[];
  armasInvolucradas: Arma[];
  comentarios: IncidenciaComentario[];
  personasInvolucradas: Persona[];
  vehiculosInvolucrados: Vehiculo[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
