import { Deserializable } from "./Deserializable";
import Modalidad from "./Modalidad";
import EmpresaModalidad from "./EmpresaModalidad";

export default class EmpresaDomicilio implements Deserializable {
  id: number;
  uuid: string;
  nombre: string;
  domicilio1: string;
  numeroExterior: string;
  numeroInterior: string;
  domicilio2: string;
  domicilio3: string;
  domicilio4: string;
  estado: string;
  pais: string;
  codigoPostal: string;
  matriz: string;
  telefonoFijo: string;
  telefonoMovil: string;
  latitud: string;
  longitud: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

  static obtenerColumnasPorDefault() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true},
      {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
      {headerName: 'Domicilio', field: 'domicilio1', sortable: true, filter: true},
      {headerName: 'Colonia', field: 'domicilio2', sortable: true, filter: true},
      {headerName: 'Estado', field: 'estado', sortable: true, filter: true},
      {headerName: 'C.P.', field: 'codigoPostal', sortable: true, filter: true}
    ];
  }

  static obtenerTodasLasColumnas() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true},
      {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
      {headerName: 'Domicilio', field: 'domicilio1', sortable: true, filter: true},
      {headerName: 'N. Ext', field: 'numeroExterior', sortable: true, filter: true},
      {headerName: 'N. Int', field: 'numeroInterior', sortable: true, filter: true},
      {headerName: 'Colonia', field: 'domicilio2', sortable: true, filter: true},
      {headerName: 'Municipio', field: 'domicilio3', sortable: true, filter: true},
      {headerName: 'Referencia', field: 'domicilio4', sortable: true, filter: true},
      {headerName: 'Estado', field: 'estado', sortable: true, filter: true},
      {headerName: 'Pais', field: 'pais', sortable: true, filter: true},
      {headerName: 'C.P.', field: 'codigoPostal', sortable: true, filter: true},
      {headerName: 'Tel. Fijo', field: 'telefonoFijo', sortable: true, filter: true},
      {headerName: 'Tel. Movil', field: 'telefonoMovil', sortable: true, filter: true}
    ]
  }
}
