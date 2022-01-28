import {Deserializable} from "./Deserializable";
import TipoInfraestructura from "./TipoInfraestructura";

export default class ClienteDomicilio implements Deserializable {

  id: number;
  uuid: string;
  nombre: string;
  domicilio1: string;
  domicilio2: string;
  domicilio3: string;
  domicilio4: string;
  estado: string;
  pais: string;
  codigoPostal: string;
  matriz: string;
  contacto: string;
  telefonoFijo: string;
  telefonoMovil: string;
  correoElectronico: string;
  tipoInfraestructura: TipoInfraestructura;
  tipoInfraestructuraOtro: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
