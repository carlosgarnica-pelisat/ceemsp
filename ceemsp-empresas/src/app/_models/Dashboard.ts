import {Deserializable} from "./Deserializable";
import Incidencia from "./Incidencia";
import Visita from "./Visita";
import Acuerdo from "./Acuerdo";


export default class Dashboard implements Deserializable {
  incidenciasPendientes: number;
  incidenciasAbiertas: number;
  incidenciasProcedentes: number;
  incidenciasImprocedentes: number;
  incidenciasContestadas: number;
  acuerdosProximosAVencer: number;
  acuerdosTotales: number;
  requerimientosProximosAVencer: number;
  personalActivo: number;
  clientesProximosAVencer: number;
  diasRestantesLicenciaFederal: number;
  licenciasColectivasProximasAVencer: number;

  requerimientosProximos: Visita[];

  proximasVisitas: Visita[];
  listaRequerimientosProximosAVencer: Visita[];
  listaAcuerdosProximosAVencer: Acuerdo[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
