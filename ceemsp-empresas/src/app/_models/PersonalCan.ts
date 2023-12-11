import {Deserializable} from "./Deserializable";
import Vehiculo from "./Vehiculo";
import Can from "./Can";
import Persona from "./Persona";

export default class PersonalCan implements Deserializable {

  id: number;
  uuid: string;
  can: Can;
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
