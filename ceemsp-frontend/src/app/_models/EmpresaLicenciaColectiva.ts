import {Deserializable} from "./Deserializable";
import Modalidad from "./Modalidad";
import Submodalidad from "./Submodalidad";
import Empresa from "./Empresa";

export default class EmpresaLicenciaColectiva implements Deserializable {
  id: number;
  uuid: string;
  numeroOficio: string;
  modalidad: Modalidad;
  submodalidad: Submodalidad;
  fechaInicio: string;
  fechaFin: string;
  rutaDocumento: string;
  eliminado: boolean;

  cantidadArmasCortas: number;
  cantidadArmasLargas: number;

  motivoBaja: string;
  observacionesBaja: string;
  documentoFundatorioBaja: string;
  fechaBaja: string;

  fechaCreacion: string;
  fechaActualizacion: string;
  empresa: Empresa;
  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

  static obtenerColumnasPorDefault() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
      {headerName: 'Numero de oficio', field: 'numeroOficio', sortable: true, filter: true },
      {headerName: 'Fecha de Inicio', field: 'fechaInicio', sortable: true, filter: true},
      {headerName: 'Fecha de Término', field: 'fechaFin', sortable: true, filter: true}
    ];
  }

  static obtenerTodasLasColumnas() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
      {headerName: 'Número de oficio', field: 'numeroOficio', sortable: true, filter: true },
      {headerName: 'Fecha de Inicio', field: 'fechaInicio', sortable: true, filter: true},
      {headerName: 'Fecha de Término', field: 'fechaFin', sortable: true, filter: true},
      {headerName: 'Modalidad', field: 'modalidad.nombre', sortable: true, filter: true},
      {headerName: 'Submodalidad', field: 'submodalidad.nombre', sortable: true, filter: true}
    ]
  }
}
