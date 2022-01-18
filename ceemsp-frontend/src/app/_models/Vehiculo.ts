import {Deserializable} from "./Deserializable";
import VehiculoTipo from "./VehiculoTipo";
import VehiculoMarca from "./VehiculoMarca";
import VehiculoSubmarca from "./VehiculoSubmarca";
import VehiculoColor from "./VehiculoColor";

export default class Vehiculo implements Deserializable {
  id: number;
  uuid: string;
  tipo: VehiculoTipo;
  marca: VehiculoMarca;
  submarca: VehiculoSubmarca;
  anio: string;
  color: string;
  rotulado: boolean;
  placas: string;
  serie: string;
  origen: string;
  blindado: boolean;
  serieBlindaje: string;
  fechaBlindaje: string;
  numeroHolograma: string;
  placaMetalica: string;
  empresaBlindaje: string;
  nivelBlindaje: string;
  colores: VehiculoColor[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

  static obtenerColumnasPorDefault() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true},
      {headerName: 'Tipo', field: 'tipo.nombre', sortable: true, filter: true },
      {headerName: 'Marca', field: 'marca.nombre', sortable: true, filter: true},
      {headerName: 'Submarca', field: 'submarca.nombre', sortable: true, filter: true},
      {headerName: 'Placas', field: 'placas', sortable: true, filter: true},
      {headerName: 'Serie', field: 'serie', sortable: true, filter: true}
    ];
  }

  static obtenerTodasLasColumnas() {
    return [
      {headerName: 'ID', field: 'uuid', sortable: true, filter: true},
      {headerName: 'Tipo', field: 'tipo.nombre', sortable: true, filter: true },
      {headerName: 'Marca', field: 'marca.nombre', sortable: true, filter: true},
      {headerName: 'Submarca', field: 'submarca.nombre', sortable: true, filter: true},
      {headerName: 'Placas', field: 'placas', sortable: true, filter: true},
      {headerName: 'Anio', field: 'anio', sortable: true, filter: true},
      {headerName: 'Serie', field: 'serie', sortable: true, filter: true},
      {headerName: 'Rotulado', field: 'rotulado', sortable: true, filter: true},
      {headerName: 'Origen', field: 'origen', sortable: true, filter: true},
      {headerName: 'Blindado', field: 'blindado', sortable: true, filter: true},
      {headerName: 'Serie Blindaje', field: 'serieBlindaje', sortable: true, filter: true},
      {headerName: 'Fecha Blindaje', field: 'fechaBlindaje', sortable: true, filter: true},
      {headerName: 'Numero holograma', field: 'numeroHolograma', sortable: true, filter: true},
      {headerName: 'Placa metalica', field: 'placaMetalica', sortable: true, filter: true},
      {headerName: 'Empresa blindaje', field: 'empresaBlindaje', sortable: true, filter: true},
      {headerName: 'Nivel blindaje', field: 'nivelBlindaje', sortable: true, filter: true}
    ]
  }
}
