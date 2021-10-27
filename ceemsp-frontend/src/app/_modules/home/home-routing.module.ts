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
import {EmpresaDomiciliosComponent} from "../../home/empresas/empresa-domicilios/empresa-domicilios.component";
import {EmpresaLegalComponent} from "../../home/empresas/empresa-legal/empresa-legal.component";
import {EmpresaLicenciasComponent} from "../../home/empresas/empresa-licencias/empresa-licencias.component";
import {EmpresaCanesComponent} from "../../home/empresas/empresa-canes/empresa-canes.component";
import {EmpresaArmasComponent} from "../../home/empresas/empresa-armas/empresa-armas.component";
import {EmpresaVehiculosComponent} from "../../home/empresas/empresa-vehiculos/empresa-vehiculos.component";
import {EmpresaIncidenciasComponent} from "../../home/empresas/empresa-incidencias/empresa-incidencias.component";


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
        path: 'empresas',
        component: EmpresasComponent
      },
      {
        path: 'empresas/nueva',
        component: EmpresaNuevaComponent
      },
      {
        path: 'empresas/:uuid',
        component: EmpresaDetallesComponent
      },
      {
        path: 'empresas/:uuid/domicilios',
        component: EmpresaDomiciliosComponent
      },
      {
        path: 'empresas/:uuid/legal',
        component: EmpresaLegalComponent
      },
      {
        path: 'empresas/:uuid/licencias',
        component: EmpresaLicenciasComponent
      },
      {
        path: 'empresas/:uuid/canes',
        component: EmpresaCanesComponent
      },
      {
        path: 'empresas/:uuid/armas',
        component: EmpresaArmasComponent
      },
      {
        path: 'empresas/:uuid/vehiculos',
        component: EmpresaVehiculosComponent
      },
      {
        path: 'empresas/:uuid/incidencias',
        component: EmpresaIncidenciasComponent
      },

      // Catalogos
      // Canes
      {
        path: 'catalogos/canes',
        component: CanesComponent
      },
      {
        path: 'catalogos/canes/entrenamientos',
        component: CanesEntrenamientosComponent
      },
      // Vehiculos
      {
        path: 'catalogos/vehiculos',
        component: VehiculosComponent
      },
      {
        path: 'catalogos/vehiculos/tipos',
        component: VehiculosTiposComponent
      },
      // Armas
      {
        path: 'catalogos/armas',
        component: ArmasComponent
      },
      {
        path: 'catalogos/armas/marcas',
        component: ArmasMarcasComponent
      },
      {
        path: 'catalogos/armas/clases',
        component: ArmasClasesComponent
      },
      // Modalidades
      {
        path: 'catalogos/modalidades',
        component: ModalidadesComponent
      },

      // Comunicados
      // Generales
      {
        path: 'comunicados/generales',
        component: ComunicadosGeneralesComponent
      },
      // Buzon salida
      {
        path: 'comunicados/buzon-salida',
        component: BuzonSalidaComponent
      },

      // Visitas
      {
        path: 'visitas',
        component: VisitasComponent
      },

      // Incidencias
      {
        path: 'incidencias',
        component: IncidenciasComponent
      },

      //Reporteo
      {
        path: 'reporteo',
        component: ReporteoComponent
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
