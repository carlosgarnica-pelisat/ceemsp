import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
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
import { AgButtonComponent } from './_components/ag-button/ag-button.component';
import { EmpresaDetallesComponent } from './home/empresas/empresa-detalles/empresa-detalles.component';
import { EmpresaDomiciliosComponent } from './home/empresas/empresa-domicilios/empresa-domicilios.component';
import { EmpresaLegalComponent } from './home/empresas/empresa-legal/empresa-legal.component';
import { EmpresaLicenciasComponent } from './home/empresas/empresa-licencias/empresa-licencias.component';
import { EmpresaCanesComponent } from './home/empresas/empresa-canes/empresa-canes.component';
import { EmpresaArmasComponent } from './home/empresas/empresa-armas/empresa-armas.component';
import { EmpresaVehiculosComponent } from './home/empresas/empresa-vehiculos/empresa-vehiculos.component';
import { EmpresaIncidenciasComponent } from './home/empresas/empresa-incidencias/empresa-incidencias.component';
import { EmpresaPersonalComponent } from './home/empresas/empresa-personal/empresa-personal.component';
import { EmpresaClientesComponent } from './home/empresas/empresa-clientes/empresa-clientes.component';
import { VehiculosUsosComponent } from './home/catalogos/vehiculos/vehiculos-usos/vehiculos-usos.component';
import { PersonalComponent } from './home/catalogos/personal/personal.component';
import { NacionalidadesComponent } from './home/catalogos/personal/nacionalidades/nacionalidades.component';
import { ComunicadoGeneralNuevoComponent } from './home/comunicados/comunicados-generales/comunicado-general-nuevo/comunicado-general-nuevo.component';
import {CKEditor4, CKEditorModule} from "ckeditor4-angular";
import { SanitizeHtmlPipe } from './_pipes/sanitize-html.pipe';
import {NgxPrintModule} from "ngx-print";
import { EmpresaEquipoComponent } from './home/empresas/empresa-equipo/empresa-equipo.component';
import { EquipoComponent } from './home/catalogos/equipo/equipo.component';
import { UsuariosComponent } from "./home/configuracion/usuarios/usuarios.component";
import { UniformesComponent } from './home/catalogos/equipo/uniformes/uniformes.component';
import { EmpresaUniformesComponent } from './home/empresas/empresa-uniformes/empresa-uniformes.component';
import {PdfViewerModule} from "ng2-pdf-viewer";
import { PhoneMaskDirective } from './_directives/phone-mask.directive';
import { NumberMaskDirective } from './_directives/number-mask.directive';
import { RfcDirective } from './_directives/rfc.directive';
import { BuscarNombrePipe } from './_pipes/buscar-nombre.pipe';
import {GoogleChartsModule} from "angular-google-charts";
import { AcercaComponent } from './home/configuracion/acerca/acerca.component';
import { BotonEmpresaDomiciliosComponent } from './_components/botones/boton-empresa-domicilios/boton-empresa-domicilios.component';
import { BotonEmpresaLegalComponent } from './_components/botones/boton-empresa-legal/boton-empresa-legal.component';
import { BotonEmpresaLicenciasComponent } from './_components/botones/boton-empresa-licencias/boton-empresa-licencias.component';
import { BotonEmpresaCanesComponent } from './_components/botones/boton-empresa-canes/boton-empresa-canes.component';
import { BotonEmpresaVehiculosComponent } from './_components/botones/boton-empresa-vehiculos/boton-empresa-vehiculos.component';
import { BotonEmpresaIncidenciasComponent } from './_components/botones/boton-empresa-incidencias/boton-empresa-incidencias.component';
import { BotonEmpresaClientesComponent } from './_components/botones/boton-empresa-clientes/boton-empresa-clientes.component';
import { BotonEmpresaPersonalComponent } from './_components/botones/boton-empresa-personal/boton-empresa-personal.component';
import { BotonEmpresaEquiposComponent } from './_components/botones/boton-empresa-equipos/boton-empresa-equipos.component';
import { BotonEmpresaUniformesComponent } from './_components/botones/boton-empresa-uniformes/boton-empresa-uniformes.component';
import { BotonCatalogosComponent } from './_components/botones/boton-catalogos/boton-catalogos.component';
import { EmpresaAcuerdosComponent } from './home/empresas/empresa-acuerdos/empresa-acuerdos.component';
import { BotonEmpresaAcuerdosComponent } from './_components/botones/boton-empresa-acuerdos/boton-empresa-acuerdos.component';
import { GoogleMapsModule } from "@angular/google-maps";
import {AgmCoreModule} from "@agm/core";
import { ModalidadArmadaPipe } from './_pipes/modalidad-armada.pipe';
import { BotonBuzonSalidaComponent } from './_components/botones/boton-buzon-salida/boton-buzon-salida.component';
import { BuscarRazonSocialPipe } from './_pipes/buscar-razon-social.pipe';
import { BuscarNombresPipe } from './_pipes/buscar-nombres.pipe';

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
    EmpresaUniformesComponent,
    PhoneMaskDirective,
    NumberMaskDirective,
    RfcDirective,
    BuscarNombrePipe,
    UsuariosComponent,
    AcercaComponent,
    BotonEmpresaDomiciliosComponent,
    BotonEmpresaLegalComponent,
    BotonEmpresaLicenciasComponent,
    BotonEmpresaCanesComponent,
    BotonEmpresaVehiculosComponent,
    BotonEmpresaIncidenciasComponent,
    BotonEmpresaClientesComponent,
    BotonEmpresaPersonalComponent,
    BotonEmpresaEquiposComponent,
    BotonEmpresaUniformesComponent,
    BotonCatalogosComponent,
    EmpresaAcuerdosComponent,
    BotonEmpresaAcuerdosComponent,
    ModalidadArmadaPipe,
    BotonBuzonSalidaComponent,
    BuscarRazonSocialPipe,
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
      apiKey: 'AIzaSyDhtDzSjzQt5q9LI8aR7xXNA4QPsUgaEsY'
    })
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
