import {Deserializable} from "./Deserializable";

export default class InformeMensual implements Deserializable {
  id: number;
  uuid: string;
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
  uniformesActivos: number;
  uniformesAltas: number;
  uniformesBajas: number;
  uniformesTotal: number;
  incidenciasReportadas: number;
  incidenciasTotal: number;
  canesAsignados: number;
  canesInstalaciones: number;
  canesAltas: number;
  canesBajas: number;
  canesTotal: number;
  armas1Altas: number;
  armas2Altas: number;
  armas3Altas: number;
  armas1Bajas: number;
  armas2Bajas: number;
  armas3Bajas: number;
  armas1Activas: number;
  armas2Activas: number;
  armas3Activas: number;
  armas1Total: number;
  armas2Total: number;
  armas3Total: number;
  reportaUniformes: boolean;
  incidenciasProcedentes: number;
  incidenciasImprocedentes: number;
  cadenaOriginal: string;
  sello: string;
  fechaCreacion: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
