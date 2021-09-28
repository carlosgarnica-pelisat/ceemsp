import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { DashboardComponent } from './home/dashboard/dashboard.component';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {JwtInterceptor} from "./_helpers/jwt.interceptor";
import {ErrorInterceptor} from "./_helpers/error.interceptor";
import {EmpresasComponent} from "./home/empresas/empresas.component";
import { EmpresaNuevaComponent } from './home/empresas/empresa-nueva/empresa-nueva.component';
import { CanesComponent } from './home/catalogos/canes/canes.component';
import { ArmasComponent } from './home/catalogos/armas/armas.component';
import { VehiculosComponent } from './home/catalogos/vehiculos/vehiculos.component';
import {AgGridModule} from "ag-grid-angular";
import { CanesEntrenamientosComponent } from './home/catalogos/canes/canes-entrenamientos/canes-entrenamientos.component';
import { IncidenciasComponent } from './home/incidencias/incidencias.component';
import { ConfiguracionComponent } from './home/configuracion/configuracion.component';
import { ArmasMarcasComponent } from './home/catalogos/armas/armas-marcas/armas-marcas.component';
import { ArmasClasesComponent } from './home/catalogos/armas/armas-clases/armas-clases.component';
import { VehiculosTiposComponent } from './home/catalogos/vehiculos/vehiculos-tipos/vehiculos-tipos.component';
import { ComunicadosGeneralesComponent } from './home/comunicados/comunicados-generales/comunicados-generales.component';
import { BuzonSalidaComponent } from './home/comunicados/buzon-salida/buzon-salida.component';
import { VisitasComponent } from './home/visitas/visitas.component';
import { ReporteoComponent } from './home/reporteo/reporteo.component';
import { ModalidadesComponent } from './home/catalogos/modalidades/modalidades.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    EmpresasComponent,
    EmpresaNuevaComponent,
    CanesComponent,
    ArmasComponent,
    VehiculosComponent,
    CanesEntrenamientosComponent,
    IncidenciasComponent,
    ConfiguracionComponent,
    ArmasMarcasComponent,
    ArmasClasesComponent,
    VehiculosTiposComponent,
    ComunicadosGeneralesComponent,
    BuzonSalidaComponent,
    VisitasComponent,
    ReporteoComponent,
    ModalidadesComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule,
    FontAwesomeModule,
    AgGridModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  exports: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
