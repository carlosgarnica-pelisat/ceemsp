import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {ValidarAcuseComponent} from "./validar-acuse/validar-acuse.component";
import {ValidarReporteComponent} from "./validar-reporte/validar-reporte.component";

const routes: Routes = [
  {
    path: 'home',
    loadChildren: () => import ('./_modules/home/home.module').then(m => m.HomeModule)
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'validar-acuse',
    component: ValidarAcuseComponent
  },
  {
    path: 'validar-reporte',
    component: ValidarReporteComponent
  },
  {
    path: '**',
    redirectTo: 'home'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: "legacy" })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
