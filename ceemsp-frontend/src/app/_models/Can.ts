import {Deserializable} from "./Deserializable";
import CanRaza from "./CanRaza";
import EmpresaDomicilio from "./EmpresaDomicilio";
import Persona from "./Persona";
import Cliente from "./Cliente";
import ClienteDomicilio from "./ClienteDomicilio";
import CanCartillaVacunacion from "./CanCartillaVacunacion";
import CanConstanciaSalud from "./CanConstanciaSalud";
import CanAdiestramiento from "./CanAdiestramiento";

export default class Can implements Deserializable {
  id: number;
  uuid: string;
  nombre: string;
  genero: string;
  raza: CanRaza;
  domicilioAsignado: EmpresaDomicilio;
  fechaIngreso: string;
  edad: number;
  peso: number;
  descripcion: string;
  origen: string;
  status: string;
  chip: boolean;
  tatuaje: boolean;
  razonSocial: string;
  fechaInicio: string;
  fechaFin: string;
  elementoAsignado: Persona;
  clienteAsignado: Cliente;
  clienteDomicilio: ClienteDomicilio;
  motivos: string;

  cartillasVacunacion: CanCartillaVacunacion[];
  constanciasSalud: CanConstanciaSalud[];
  adiestramientos: CanAdiestramiento[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
