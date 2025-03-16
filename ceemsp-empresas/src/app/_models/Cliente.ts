import {Deserializable} from "./Deserializable";
import ClienteDomicilio from "./ClienteDomicilio";
import ClienteAsignacionPersonal from "./ClienteAsignacionPersonal";
import ClienteModalidad from "./ClienteModalidad";
import ClienteFormaEjecucion from "./ClienteFormaEjecucion";

export default class Cliente implements Deserializable {

  id: number;
  uuid: string;
  tipoPersona: string;
  rfc: string;
  nombreComercial: string;
  razonSocial: string;
  canes: boolean;
  armas: boolean;
  rutaArchivoContrato: string;
  fechaInicio: string;
  fechaFin: string;
  fechaCreacion: string;
  fechaActualizacion: string;
  eliminado: boolean;

  motivoBaja: string;
  observacionesBaja: string;
  documentoFundatorioBaja: string;
  fechaBaja: string;

  numeroSucursales: number;
  numeroElementosAsignados: number;

  domicilios: ClienteDomicilio[];
  asignaciones: ClienteAsignacionPersonal[];
  modalidades: ClienteModalidad[];
  formasEjecucion: ClienteFormaEjecucion[];

  domicilioCapturado: boolean;
  asignacionCapturada: boolean;
  modalidadCapturada: boolean;
  formaEjecucionCapturada: boolean;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

  static obtenerColumnasPorDefault() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true},
      {headerName: 'Tipo persona', field: 'tipoPersona', sortable: true, filter: true },
      {headerName: 'RFC', field: 'rfc', sortable: true, filter: true},
      {headerName: 'Nombre comercial', field: 'nombreComercial', sortable: true, filter: true},
      {headerName: 'Razon social', field: 'razonSocial', sortable: true, filter: true}
    ];
  }

  static obtenerTodasLasColumnas() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true},
      {headerName: 'Tipo persona', field: 'tipoPersona', sortable: true, filter: true },
      {headerName: 'RFC', field: 'rfc', sortable: true, filter: true},
      {headerName: 'Nombre comercial', field: 'nombreComercial', sortable: true, filter: true},
      {headerName: 'Razon social', field: 'razonSocial', sortable: true, filter: true},
      {headerName: 'Canes', field: 'canes', sortable: true, filter: true},
      {headerName: 'Armas', field: 'armas', sortable: true, filter: true},
      {headerName: 'Fecha Inicio', field: 'fechaInicio', sortable: true, filter: true},
      {headerName: 'Fecha Fin', field: 'fechaFin', sortable: true, filter: true},
    ]
  }
}
