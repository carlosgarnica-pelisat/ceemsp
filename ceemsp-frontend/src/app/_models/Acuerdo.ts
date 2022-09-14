import {Deserializable} from "./Deserializable";

export default class Acuerdo implements Deserializable {
  id: number;
  uuid: string;
  rutaArchivo: string;
  fecha: string;
  observaciones: string;
  eliminado: boolean;
  motivoBaja: string;
  observacionesBaja: string;
  documentoFundatorioBaja: string;
  fechaBaja: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

  static obtenerColumnasPorDefault() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
      {headerName: 'Fecha', field: 'nombre', sortable: true, filter: true },
      {headerName: 'Observaciones', field: 'descripcion', sortable: true, filter: true}
    ];
  }

  static obtenerTodasLasColumnas() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
      {headerName: 'Fecha', field: 'nombre', sortable: true, filter: true },
      {headerName: 'Observaciones', field: 'descripcion', sortable: true, filter: true}
    ];
  }

}
