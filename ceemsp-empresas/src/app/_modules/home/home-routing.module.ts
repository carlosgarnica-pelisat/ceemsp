import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "../../home/home.component";
import {NgModule} from "@angular/core";
import {AuthGuard} from "../../_helpers/auth.guard";
import {DashboardComponent} from "../../home/dashboard/dashboard.component";
import {EmpresasComponent} from "../../home/empresas/empresas.component";
import {EmpresaNuevaComponent} from "../../home/empresas/empresa-nueva/empresa-nueva.component";
import {VehiculosComponent} from "../../home/catalogos/vehiculos/vehiculos.component";
import {ArmasComponent} from "../../home/catalogos/armas/armas.component";
import {CanesComponent} from "../../home/catalogos/canes/canes.component";
import {CanesEntrenamientosComponent} from "../../home/catalogos/canes/canes-entrenamientos/canes-entrenamientos.component";
import {ArmasMarcasComponent} from "../../home/catalogos/armas/armas-marcas/armas-marcas.component";
import {ArmasClasesComponent} from "../../home/catalogos/armas/armas-clases/armas-clases.component";
import {VehiculosTiposComponent} from "../../home/catalogos/vehiculos/vehiculos-tipos/vehiculos-tipos.component";
import {ComunicadosGeneralesComponent} from "../../home/comunicados/comunicados-generales/comunicados-generales.component";
import {BuzonSalidaComponent} from "../../home/comunicados/buzon-salida/buzon-salida.component";
import {VisitasComponent} from "../../home/visitas/visitas.component";
import {IncidenciasComponent} from "../../home/incidencias/incidencias.component";
import {ReporteoComponent} from "../../home/reporteo/reporteo.component";
import {ConfiguracionComponent} from "../../home/configuracion/configuracion.component";
import {ModalidadesComponent} from "../../home/catalogos/modalidades/modalidades.component";
import {EmpresaDetallesComponent} from "../../home/empresas/empresa-detalles/empresa-detalles.component";
import {EmpresaDomiciliosComponent} from "../../home/empresa-domicilios/empresa-domicilios.component";
import {EmpresaLegalComponent} from "../../home/empresa-legal/empresa-legal.component";
import {EmpresaLicenciasComponent} from "../../home/empresa-licencias/empresa-licencias.component";
import {EmpresaCanesComponent} from "../../home/empresa-canes/empresa-canes.component";
import {EmpresaArmasComponent} from "../../home/empresa-armas/empresa-armas.component";
import {EmpresaVehiculosComponent} from "../../home/empresa-vehiculos/empresa-vehiculos.component";
import {EmpresaIncidenciasComponent} from "../../home/empresa-incidencias/empresa-incidencias.component";
import {EmpresaPersonalComponent} from "../../home/empresa-personal/empresa-personal.component";
import {EmpresaClientesComponent} from "../../home/empresa-clientes/empresa-clientes.component";
import {VehiculosUsosComponent} from "../../home/catalogos/vehiculos/vehiculos-usos/vehiculos-usos.component";
import {PersonalComponent} from "../../home/catalogos/personal/personal.component";
import {NacionalidadesComponent} from "../../home/catalogos/personal/nacionalidades/nacionalidades.component";
import {ComunicadoGeneralNuevoComponent} from "../../home/comunicados/comunicados-generales/comunicado-general-nuevo/comunicado-general-nuevo.component";
import {EmpresaEquipoComponent} from "../../home/empresa-equipo/empresa-equipo.component";
import {EquipoComponent} from "../../home/catalogos/equipo/equipo.component";
import {UniformesComponent} from "../../home/catalogos/equipo/uniformes/uniformes.component";
import {EmpresaUniformesComponent} from "../../home/empresa-uniformes/empresa-uniformes.component";


const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        component: DashboardComponent,
      },
      // Empresas
      {
        path: 'domicilios',
        component: EmpresaDomiciliosComponent
      },
      {
        path: 'legal',
        component: EmpresaLegalComponent
      },
      {
        path: 'licencias',
        component: EmpresaLicenciasComponent
      },
      {
        path: 'canes',
        component: EmpresaCanesComponent
      },
      {
        path: 'vehiculos',
        component: EmpresaVehiculosComponent
      },
      {
        path: 'incidencias',
        component: EmpresaIncidenciasComponent
      },
      {
        path: 'clientes',
        component: EmpresaClientesComponent
      },
      {
        path: 'personal',
        component: EmpresaPersonalComponent
      },
      {
        path: 'equipo',
        component: EmpresaEquipoComponent
      },
      {
        path: 'uniformes',
        component: EmpresaUniformesComponent
      },

      // Configuracion
      {
        path: 'configuracion',
        component: ConfiguracionComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HomeRoutingModule { }
