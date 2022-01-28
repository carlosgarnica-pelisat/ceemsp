import {Deserializable} from "./Deserializable";

export default class PersonalNacionalidad implements Deserializable {

  id: number;
  uuid: string;
  nombre: string;
  descripcion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
