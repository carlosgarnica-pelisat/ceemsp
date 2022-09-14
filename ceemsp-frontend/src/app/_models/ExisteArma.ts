import {Deserializable} from "./Deserializable";
import Arma from "./Arma";

export default class ExisteArma implements Deserializable {

  existe: boolean;
  serie: string;
  matricula: string;
  arma: Arma;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
