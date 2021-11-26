import { Deserializable } from "./Deserializable";
import Modalidad from "./Modalidad";
import EmpresaModalidad from "./EmpresaModalidad";

export default class EmpresaDomicilio implements Deserializable {
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
  telefonoFijo: string;
  telefonoMovil: string;
  latitud: string;
  longitud: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
