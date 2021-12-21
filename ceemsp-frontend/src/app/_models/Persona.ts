import {Deserializable} from "./Deserializable";
import PersonalNacionalidad from "./PersonalNacionalidad";
import PersonaCertificacion from "./PersonaCertificacion";

export default class Persona implements Deserializable {
  id: string;
  uuid: string;
  nacionalidad: PersonalNacionalidad;
  curp: string;
  apellidoPaterno: string;
  apellidoMaterno: string;
  nombres: string;
  sexo: string;
  fechaDeNacimiento: string;
  tipoSangre: string;
  estadoCivil: string;
  domicilio1: string;
  domicilio2: string;
  domicilio3: string;
  domicilio4: string;
  estado: string;
  pais: string;
  codigoPostal: string;
  telefono: string;
  correoElectronico: string;

  certificaciones: PersonaCertificacion[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
