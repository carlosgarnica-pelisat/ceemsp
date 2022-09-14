import {Deserializable} from "./Deserializable";
import Usuario from "./EmpresaEscritura";

export default class ExisteUsuario implements Deserializable {

  existe: boolean;
  email: string;
  username: string;
  usuario: Usuario;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
