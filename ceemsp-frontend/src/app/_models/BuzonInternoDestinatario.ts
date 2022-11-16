import {Deserializable} from "./Deserializable";
import Usuario from "./Usuario";
import Empresa from "./Empresa";

export default class BuzonInternoDestinatario implements Deserializable {
  id: number;
  uuid: string;
  tipoDestinatario: string;
  email: string;
  usuario: Usuario;
  empresa: Empresa;
  visto: boolean;
  fechaVisto: string;
  fechaActualizacion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
