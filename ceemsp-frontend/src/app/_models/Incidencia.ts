import {Deserializable} from "./Deserializable";
import Cliente from "./Cliente";
import Usuario from "./Usuario";
import Can from "./Can";
import Arma from "./Arma";
import IncidenciaComentario from "./IncidenciaComentario";
import Persona from "./Persona";
import Vehiculo from "./Vehiculo";
import IncidenciaArchivoMetadata from "./IncidenciaArchivoMetadata";

export default class Incidencia implements Deserializable {
  id: number;
  uuid: string;
  numero: string;
  fechaIncidencia: string;
  fechaCreacion: string;
  cliente: Cliente;
  status: string;
  asignado: Usuario;
  latitud: string;
  longitud: string;

  canesInvolucrados: Can[];
  armasInvolucradas: Arma[];
  comentarios: IncidenciaComentario[];
  personasInvolucradas: Persona[];
  vehiculosInvolucrados: Vehiculo[];
  archivos: IncidenciaArchivoMetadata[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
