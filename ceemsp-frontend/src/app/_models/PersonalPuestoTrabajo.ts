import {Deserializable} from "./Deserializable";
import PersonalSubpuestoTrabajo from "./PersonalSubpuestoTrabajo";

export default class PersonalPuestoTrabajo implements Deserializable {
  id: number;
  uuid: string;
  nombre: string;
  descripcion: string;
  subpuestos: PersonalSubpuestoTrabajo[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
