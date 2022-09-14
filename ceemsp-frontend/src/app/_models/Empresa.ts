import { Deserializable } from "./Deserializable";
import Modalidad from "./Modalidad";
import EmpresaModalidad from "./EmpresaModalidad";
import EmpresaFormaEjecucion from "./EmpresaFormaEjecucion";
import Usuario from "./Usuario";

export default class Empresa implements Deserializable {
  id: number;
  uuid: string;
  razonSocial: string;
  nombreComercial: string;
  tipoTramite: string;
  registro: string;
  tipoPersona: string;
  rfc: string;
  curp: string;
  sexo: string;
  correoElectronico: string;
  telefono: string;
  status: string;
  observaciones: string;
  tieneArmas: boolean;
  tieneCanes: boolean;
  registroFederal: string;
  fechaInicio: string;
  fechaFin: string;

  formasEjecucion: EmpresaFormaEjecucion[];
  modalidades: EmpresaModalidad[];

  usuario: Usuario;

  fechaCreacion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

  static obtenerColumnasPorDefault() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
      {headerName: 'Nombre comercial', field: 'nombreComercial', sortable: true, filter: true },
      {headerName: 'Registro', field: 'registro', sortable: true, filter: true },
      {headerName: 'Razon social', field: 'razonSocial', sortable: true, filter: true},
      {headerName: 'Tipo persona', field: 'tipoPersona', sortable: true, filter: true}
    ];
  }

  static obtenerTodasLasColumnas() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
      {headerName: 'Nombre comercial', field: 'nombreComercial', sortable: true, filter: true },
      {headerName: 'Registro', field: 'registro', sortable: true, filter: true },
      {headerName: 'Razon social', field: 'razonSocial', sortable: true, filter: true},
      {headerName: 'Tipo persona', field: 'tipoPersona', sortable: true, filter: true},
      {headerName: 'RFC', field: 'rfc', sortable: true, filter: true},
      {headerName: 'CURP', field: 'curp', sortable: true, filter: true},
      {headerName: 'Sexo', field: 'sexo', sortable: true, filter: true},
      {headerName: 'Correo electronico', field: 'correoElectronico', sortable: true, filter: true},
      {headerName: 'Telefono', field: 'telefono', sortable: true, filter: true}
    ]
  }
}
