import {Deserializable} from "./Deserializable";
import Modalidad from "./Modalidad";
import Submodalidad from "./Submodalidad";
import Visita from "./Visita";
import Acuerdo from "./Acuerdo";
import Empresa from "./Empresa";
import EmpresaEscrituraApoderado from "./EmpresaEscrituraApoderado";
import EmpresaLicenciaColectiva from "./EmpresaLicenciaColectiva";
import Incidencia from "./Incidencia";

export default class Dashboard implements Deserializable {
  incidenciasAbiertas: Incidencia[];
  misIncidencias: Incidencia[];
  cantidadRequerimientosProximosAVencer: number;
  cantidadLicenciasFederalesProximasAVencer: number;
  visitasDelMesPasadas: number;
  visitasDelMesTotales: number;
  cantidadAcuerdosProximosAVencer: number;
  cantidadLicenciasParticularesProximasAVencer: number;

  totalEmpresasRegistradas: number;
  empresasAutorizacionProvisional: number;
  empresasRegistroFederal: number;
  empresasAutorizacionEstatal: number;
  empresasServiciosPropios: number;
  empresasActivas: number;
  empresasPerdidaEficacia: number;
  empresasSuspendidas: number;
  empresasRevocadas: number;
  empresasClausuradas: number;

  proximasVisitas: Visita[];
  requerimientosProximosAVencer: Visita[];
  acuerdosProximosAVencer: Acuerdo[];
  licenciasFederalesProximasAVencer: Empresa[];
  empresasConInformeMensual: Empresa[];
  empresasSinInformeMensual: Empresa[];
  apoderadosProximosAVencer: EmpresaEscrituraApoderado[];
  licenciasParticularesProximasAVencer: EmpresaLicenciaColectiva[];

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
