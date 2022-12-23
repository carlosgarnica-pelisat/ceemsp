import {Deserializable} from "./Deserializable";
import Vehiculo from "./Vehiculo";
import Arma from "./Arma";

export default class PersonalArma implements Deserializable {

  id: number;
  uuid: string;
  arma: Arma;
  observaciones: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
