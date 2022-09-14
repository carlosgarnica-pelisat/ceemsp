import {Deserializable} from "./Deserializable";
import CanRaza from "./CanRaza";
import EmpresaDomicilio from "./EmpresaDomicilio";
import Persona from "./Persona";
import Cliente from "./Cliente";
import ClienteDomicilio from "./ClienteDomicilio";
import CanCartillaVacunacion from "./CanCartillaVacunacion";
import CanConstanciaSalud from "./CanConstanciaSalud";
import CanAdiestramiento from "./CanAdiestramiento";
import CanFotografia from "./CanFotografia";

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

  motivoBaja: string;
  observacionesBaja: string;
  documentoFundatorioBaja: string;
  fechaBaja: string;

  cartillasVacunacion: CanCartillaVacunacion[];
  constanciasSalud: CanConstanciaSalud[];
  adiestramientos: CanAdiestramiento[];
  fotografias: CanFotografia[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

  static obtenerColumnasPorDefault() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
      {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
      {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true},
      {headerName: 'Status', field: 'status', sortable: true, filter: true}
    ];
  }

  static obtenerTodasLasColumnas() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
      {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
      {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true},
      {headerName: 'Status', field: 'status', sortable: true, filter: true},
      {headerName: 'Genero', field: 'genero', sortable: true, filter: true},
      {headerName: 'Raza', field: 'raza.nombre', sortable: true, filter: true},
      {headerName: 'Edad', field: 'edad', sortable: true, filter: true},
      {headerName: 'Peso', field: 'peso', sortable: true, filter: true},
      {headerName: 'Chip', field: 'chip', sortable: true, filter: true},
      {headerName: 'Status', field: 'status', sortable: true, filter: true}
    ]
  }


}
