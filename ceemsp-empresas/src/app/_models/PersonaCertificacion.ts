import {Deserializable} from "./Deserializable";

export default class PersonaCertificacion implements Deserializable {
  nombre: string;
  nombreInstructor: string;
  duracion: string;
  fechaInicio: string;
  fechaFin: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
