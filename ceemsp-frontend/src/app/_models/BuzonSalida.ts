import {Deserializable} from "./Deserializable";
import Usuario from "./Usuario";
import Empresa from "./Empresa";
import BuzonInternoDestinatario from "./BuzonInternoDestinatario";

export default class BuzonSalida implements Deserializable {
  id: number;
  uuid: string;
  motivo: string;
  mensaje: string;
  destinatarios: BuzonInternoDestinatario[];
  fechaCreacion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
