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
import { ToastComponent } from './_components/toast/toast.component';
import { AgButtonComponent } from './_components/ag-button/ag-button.component';
import { EmpresaDetallesComponent } from './home/empresas/empresa-detalles/empresa-detalles.component';
import { EmpresaDomiciliosComponent } from './home/empresa-domicilios/empresa-domicilios.component';
import { EmpresaLegalComponent } from './home/empresa-legal/empresa-legal.component';
import { EmpresaLicenciasComponent } from './home/empresa-licencias/empresa-licencias.component';
import { EmpresaCanesComponent } from './home/empresa-canes/empresa-canes.component';
import { EmpresaArmasComponent } from './home/empresa-armas/empresa-armas.component';
import { EmpresaVehiculosComponent } from './home/empresa-vehiculos/empresa-vehiculos.component';
import { EmpresaIncidenciasComponent } from './home/empresa-incidencias/empresa-incidencias.component';
import { EmpresaPersonalComponent } from './home/empresa-personal/empresa-personal.component';
import { EmpresaClientesComponent } from './home/empresa-clientes/empresa-clientes.component';
import { VehiculosUsosComponent } from './home/catalogos/vehiculos/vehiculos-usos/vehiculos-usos.component';
import { PersonalComponent } from './home/catalogos/personal/personal.component';
import { NacionalidadesComponent } from './home/catalogos/personal/nacionalidades/nacionalidades.component';
import { ComunicadoGeneralNuevoComponent } from './home/comunicados/comunicados-generales/comunicado-general-nuevo/comunicado-general-nuevo.component';
import {CKEditor4, CKEditorModule} from "ckeditor4-angular";
import { SanitizeHtmlPipe } from './_pipes/sanitize-html.pipe';
import {NgxPrintModule} from "ngx-print";
import { EmpresaEquipoComponent } from './home/empresa-equipo/empresa-equipo.component';
import { EquipoComponent } from './home/catalogos/equipo/equipo.component';
import { UniformesComponent } from './home/catalogos/equipo/uniformes/uniformes.component';
import { EmpresaUniformesComponent } from './home/empresa-uniformes/empresa-uniformes.component';

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
    ModalidadesComponent,
    AgButtonComponent,
    EmpresaDetallesComponent,
    EmpresaDomiciliosComponent,
    EmpresaLegalComponent,
    EmpresaLicenciasComponent,
    EmpresaCanesComponent,
    EmpresaArmasComponent,
    EmpresaVehiculosComponent,
    EmpresaIncidenciasComponent,
    EmpresaPersonalComponent,
    EmpresaClientesComponent,
    VehiculosUsosComponent,
    PersonalComponent,
    NacionalidadesComponent,
    ComunicadoGeneralNuevoComponent,
    SanitizeHtmlPipe,
    EmpresaEquipoComponent,
    EquipoComponent,
    UniformesComponent,
    EmpresaUniformesComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule,
    FontAwesomeModule,
    AgGridModule,
    CKEditorModule,
    NgxPrintModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    SanitizeHtmlPipe
  ],
  exports: [SanitizeHtmlPipe],
  bootstrap: [AppComponent]
})
export class AppModule { }
