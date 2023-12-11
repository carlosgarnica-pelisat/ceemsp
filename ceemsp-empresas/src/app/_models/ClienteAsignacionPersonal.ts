import {Deserializable} from "./Deserializable";
import ClienteDomicilio from "./ClienteDomicilio";
import Persona from "./Persona";

export default class ClienteAsignacionPersonal implements Deserializable {

  id: number;
  uuid: string;
  domicilio: ClienteDomicilio;
  personal: Persona;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
