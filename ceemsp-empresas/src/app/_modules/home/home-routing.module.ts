import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "../../home/home.component";
import {NgModule} from "@angular/core";
import {AuthGuard} from "../../_helpers/auth.guard";
import {DashboardComponent} from "../../home/dashboard/dashboard.component";
import {ConfiguracionComponent} from "../../home/configuracion/configuracion.component";
import {ModalidadesComponent} from "../../home/catalogos/modalidades/modalidades.component";
import {EmpresaDetallesComponent} from "../../home/empresas/empresa-detalles/empresa-detalles.component";
import {EmpresaDomiciliosComponent} from "../../home/empresa-domicilios/empresa-domicilios.component";
import {EmpresaLegalComponent} from "../../home/empresa-legal/empresa-legal.component";
import {EmpresaLicenciasComponent} from "../../home/empresa-licencias/empresa-licencias.component";
import {EmpresaCanesComponent} from "../../home/empresa-canes/empresa-canes.component";
import {EmpresaVehiculosComponent} from "../../home/empresa-vehiculos/empresa-vehiculos.component";
import {EmpresaIncidenciasComponent} from "../../home/empresa-incidencias/empresa-incidencias.component";
import {EmpresaPersonalComponent} from "../../home/empresa-personal/empresa-personal.component";
import {EmpresaClientesComponent} from "../../home/empresa-clientes/empresa-clientes.component";
import {EmpresaEquipoComponent} from "../../home/empresa-equipo/empresa-equipo.component";
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
