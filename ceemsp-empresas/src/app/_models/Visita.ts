import {Deserializable} from "./Deserializable";
import Empresa from "./Empresa";
import Usuario from "./Usuario";

export default class Visita implements Deserializable {
  id: number;
  uuid: string;
  empresa: Empresa;
  tipo: string;
  numeroRegistro: string;
  numeroOrden: string;
  fechaVisita: string;
  requerimiento: boolean;
  observaciones: string;
  fechaTermino: string;
  responsable: Usuario;
  domicilio1: string;
  numeroExterior: string;
  numeroInterior: string;
  domicilio2: string;
  domicilio3: string;
  domicilio4: string;
  estado: string;
  pais: string;
  codigoPostal: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
