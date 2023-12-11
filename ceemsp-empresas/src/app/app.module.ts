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
import { AcercaComponent } from './app/home/configuracion/acerca/acerca.component';
import {BuscarNombrePipe} from "./_pipes/buscar-nombre.pipe";
import {PdfViewerModule} from "ng2-pdf-viewer";
import { BotonEmpresaAcuerdosComponent } from './_components/botones/boton-empresa-acuerdos/boton-empresa-acuerdos.component';
import { BotonEmpresaCanesComponent } from './_components/botones/boton-empresa-canes/boton-empresa-canes.component';
import { BotonEmpresaClientesComponent } from './_components/botones/boton-empresa-clientes/boton-empresa-clientes.component';
import { BotonEmpresaDomiciliosComponent } from './_components/botones/boton-empresa-domicilios/boton-empresa-domicilios.component';
import { BotonEmpresaEquiposComponent } from './_components/botones/boton-empresa-equipos/boton-empresa-equipos.component';
import { BotonEmpresaIncidenciasComponent } from './_components/botones/boton-empresa-incidencias/boton-empresa-incidencias.component';
import { BotonEmpresaLegalComponent } from './_components/botones/boton-empresa-legal/boton-empresa-legal.component';
import { BotonEmpresaLicenciasComponent } from './_components/botones/boton-empresa-licencias/boton-empresa-licencias.component';
import { BotonEmpresaPersonalComponent } from './_components/botones/boton-empresa-personal/boton-empresa-personal.component';
import { BotonEmpresaUniformeComponent } from './_components/botones/boton-empresa-uniforme/boton-empresa-uniforme.component';
import { BotonEmpresaVehiculosComponent } from './_components/botones/boton-empresa-vehiculos/boton-empresa-vehiculos.component';
import {GoogleChartsModule} from "angular-google-charts";
import {AgmCoreModule} from "@agm/core";
import {DataTablesModule} from "angular-datatables";
import {TableModule} from "primeng/table";
import { GoogleMapsModule } from "@angular/google-maps";
import {PhoneMaskDirective} from "./_directives/phone-mask.directive";
import { EmpresaAcuerdosComponent } from './home/empresa-acuerdos/empresa-acuerdos.component';
import { ModalidadArmadaPipe } from './_pipes/modalidad-armada.pipe';
import { EmpresaInformacionComponent } from './home/empresa-informacion/empresa-informacion.component';
import { EmpresaVisitasComponent } from './home/empresa-visitas/empresa-visitas.component';
import { EmpresaReportesMensualesComponent } from './home/empresa-reportes-mensuales/empresa-reportes-mensuales.component';
import { BotonEmpresaVisitasComponent } from './_components/botones/boton-empresa-visitas/boton-empresa-visitas.component';
import { BotonEmpresaReportesComponent } from './_components/botones/boton-empresa-reportes/boton-empresa-reportes.component';
import { BusquedaComponent } from './home/busqueda/busqueda.component';
import { BuscarNombresPipe } from './_pipes/buscar-nombres.pipe';

@NgModule({
  declarations: [
    PhoneMaskDirective,
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
    EmpresaUniformesComponent,
    AcercaComponent,
    BuscarNombrePipe,
    BotonEmpresaAcuerdosComponent,
    BotonEmpresaCanesComponent,
    BotonEmpresaClientesComponent,
    BotonEmpresaDomiciliosComponent,
    BotonEmpresaEquiposComponent,
    BotonEmpresaIncidenciasComponent,
    BotonEmpresaLegalComponent,
    BotonEmpresaLicenciasComponent,
    BotonEmpresaPersonalComponent,
    BotonEmpresaUniformeComponent,
    BotonEmpresaVehiculosComponent,
    EmpresaAcuerdosComponent,
    ModalidadArmadaPipe,
    EmpresaInformacionComponent,
    EmpresaVisitasComponent,
    EmpresaReportesMensualesComponent,
    BotonEmpresaVisitasComponent,
    BotonEmpresaReportesComponent,
    BusquedaComponent,
    BuscarNombresPipe
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
    NgxPrintModule,
    PdfViewerModule,
    GoogleChartsModule,
    GoogleMapsModule,
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyB7dj4T5sXyKfmMfAjivi4lJnndOV_T5yY',
      libraries: ['geometry', 'places']
    }),
    DataTablesModule,
    TableModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    SanitizeHtmlPipe,
    BuscarNombrePipe
  ],
  exports: [SanitizeHtmlPipe, PhoneMaskDirective],
  bootstrap: [AppComponent]
})
export class AppModule { }
