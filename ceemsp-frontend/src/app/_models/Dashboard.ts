import {Deserializable} from "./Deserializable";
import Modalidad from "./Modalidad";
import Submodalidad from "./Submodalidad";
import Visita from "./Visita";

export default class Dashboard implements Deserializable {
  incidenciasAbiertas: number;
  visitasDelMesPasadas: number;
  visitasDelMesTotales: number;

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

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
