import {Deserializable} from "./Deserializable";
import Vehiculo from "./Vehiculo";
import Arma from "./Arma";
import Persona from "./Persona";

export default class PersonalArma implements Deserializable {

  id: number;
  uuid: string;
  arma: Arma;
  persona: Persona;
  observaciones: string;
  motivoBajaAsignacion: string;
  fechaCreacion: string;
  fechaActualizacion: string;
  eliminado: boolean;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
