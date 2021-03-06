import {Deserializable} from "./Deserializable";
import EmpresaEscrituraSocio from "./EmpresaEscrituraSocio";
import EmpresaEscrituraApoderado from "./EmpresaEscrituraApoderado";
import EmpresaEscrituraRepresentante from "./EmpresaEscrituraRepresentante";
import EmpresaEscrituraConsejo from "./EmpresaEscrituraConsejo";

export default class EmpresaEscritura implements Deserializable {
  id: number;
  uuid: string;
  numeroEscritura: string;
  fechaEscritura: string;
  ciudad: string;
  tipoFedatario: string;
  numero: string;
  nombreFedatario: string;
  descripcion: string;
  socios: EmpresaEscrituraSocio[];
  apoderados: EmpresaEscrituraApoderado[];
  representantes: EmpresaEscrituraRepresentante[];
  consejos: EmpresaEscrituraConsejo[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

  static obtenerColumnasPorDefault() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true},
      {headerName: 'No. Escritura', field: 'numeroEscritura', sortable: true, filter: true },
      {headerName: 'Nombre', field: 'nombreFedatario', sortable: true, filter: true},
      {headerName: 'Tipo', field: 'tipoFedatario', sortable: true, filter: true},
      {headerName: 'Numero', field: 'numero', sortable: true, filter: true},
      {headerName: 'Ciudad', field: 'ciudad', sortable: true, filter: true}
    ];
  }

  static obtenerTodasLasColumnas() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true},
      {headerName: 'No. Escritura', field: 'numeroEscritura', sortable: true, filter: true },
      {headerName: 'Nombre', field: 'nombreFedatario', sortable: true, filter: true},
      {headerName: 'Tipo', field: 'tipoFedatario', sortable: true, filter: true},
      {headerName: 'Numero', field: 'numero', sortable: true, filter: true},
      {headerName: 'Ciudad', field: 'ciudad', sortable: true, filter: true}
    ]
  }
}
