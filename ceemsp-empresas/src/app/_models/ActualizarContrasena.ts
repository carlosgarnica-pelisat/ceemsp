import {Deserializable} from "./Deserializable";

export default class ActualizarContrasena implements Deserializable {
  actualPassword: string;
  password: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
