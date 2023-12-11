import {Deserializable} from "./Deserializable";
import TipoInfraestructura from "./TipoInfraestructura";
import Estado from "./Estado";
import Municipio from "./Municipio";
import Localidad from "./Localidad";
import Colonia from "./Colonia";
import Calle from "./Calle";

export default class ClienteDomicilio implements Deserializable {

  id: number;
  uuid: string;
  nombre: string;
  domicilio1: string;
  numeroExterior: string;
  numeroInterior: string;
  domicilio2: string;
  domicilio3: string;
  domicilio4: string;
  localidad: string;
  estado: string;
  pais: string;
  codigoPostal: string;
  matriz: boolean;
  contacto: string;
  telefonoFijo: string;
  telefonoMovil: string;
  correoElectronico: string;
  tipoInfraestructura: TipoInfraestructura;
  tipoInfraestructuraOtro: string;
  estadoCatalogo: Estado;
  municipioCatalogo: Municipio;
  localidadCatalogo: Localidad;
  coloniaCatalogo: Colonia;
  calleCatalogo: Calle;
  apellidoPaternoContacto: string;
  apellidoMaternoContacto: string;
  latitud: string;
  longitud: string;
  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
