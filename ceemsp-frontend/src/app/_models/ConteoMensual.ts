import {Deserializable} from "./Deserializable";

export default class ConteoMensual implements Deserializable {
  personalActivos: number;
  personalAltas: number;
  personalBajas: number;
  personalTotal: number;
  clientesActivos: number;
  clientesAltas: number;
  clientesBajas: number;
  clientesTotal: number;
  vehiculosActivos: number;
  vehiculosAltas: number;
  vehiculosBajas: number;
  vehiculosTotal: number;
  equipoActivos: number;
  equipoAltas: number;
  equipoBajas: number;
  equipoTotal: number;
  canesAsignados: number;
  canesInstalaciones: number;
  canesAltas: number;
  canesBajas: number;
  canesTotal: number;
  armas1Activas: number;
  armas1Altas: number;
  armas1Bajas: number;
  armas1Total: number;
  armas2Activas: number;
  armas2Altas: number;
  armas2Bajas: number;
  armas2Total: number;
  armas3Activas: number;
  armas3Altas: number;
  armas3Bajas: number;
  armas3Total: number;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
