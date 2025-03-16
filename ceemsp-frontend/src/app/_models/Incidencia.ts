import {Deserializable} from "./Deserializable";
import Cliente from "./Cliente";
import Usuario from "./Usuario";
import Can from "./Can";
import Arma from "./Arma";
import IncidenciaComentario from "./IncidenciaComentario";
import Persona from "./Persona";
import Vehiculo from "./Vehiculo";
import IncidenciaArchivoMetadata from "./IncidenciaArchivoMetadata";
import ClienteDomicilio from "./ClienteDomicilio";
import Empresa from "./Empresa";

export default class Incidencia implements Deserializable {
  id: number;
  uuid: string;
  numero: string;
  fechaIncidencia: string;
  fechaCreacion: string;
  fechaActualizacion: string;
  cliente: Cliente;
  clienteDomicilio: ClienteDomicilio;
  status: string;
  asignado: Usuario;
  latitud: string;
  longitud: string;
  eliminado: boolean;

  canesInvolucrados: Can[];
  armasInvolucradas: Arma[];
  comentarios: IncidenciaComentario[];
  personasInvolucradas: Persona[];
  vehiculosInvolucrados: Vehiculo[];
  archivos: IncidenciaArchivoMetadata[];
  empresa: Empresa;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
