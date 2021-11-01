import {Deserializable} from "./Deserializable";
import VehiculoTipo from "./VehiculoTipo";
import VehiculoMarca from "./VehiculoMarca";
import VehiculoSubmarca from "./VehiculoSubmarca";

export default class Vehiculo implements Deserializable {
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

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
