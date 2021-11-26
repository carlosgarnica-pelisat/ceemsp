import {Deserializable} from "./Deserializable";
import PersonalNacionalidad from "./PersonalNacionalidad";

export default class Persona implements Deserializable {
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

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
