import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {HomeComponent} from "../../home/home.component";
import {DashboardComponent} from "../../home/dashboard/dashboard.component";
import {HomeRoutingModule} from "./home-routing.module";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {AgGridModule} from "ag-grid-angular";
import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {JwtInterceptor} from "../../_helpers/jwt.interceptor";
import {ToastComponent} from "../../_components/toast/toast.component";



@NgModule({
  declarations: [
    HomeComponent,
    DashboardComponent,
    ToastComponent
  ],
  imports: [
    CommonModule,
    HomeRoutingModule,
    NgbModule,
    ReactiveFormsModule,
    FormsModule,
    FontAwesomeModule,
    AgGridModule.withComponents([])
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ],
  exports: [
    ToastComponent
  ]
})
export class HomeModule { }
