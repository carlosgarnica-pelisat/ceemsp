import {Deserializable} from "./Deserializable";
import Vehiculo from "./Vehiculo";
import Persona from "./Persona";

export default class PersonalVehiculo implements Deserializable {

  id: number;
  uuid: string;
  vehiculo: Vehiculo;
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
