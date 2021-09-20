import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "../../home/home.component";
import {NgModule} from "@angular/core";
import {AuthGuard} from "../../_helpers/auth.guard";
import {DashboardComponent} from "../../home/dashboard/dashboard.component";


const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        component: DashboardComponent,
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HomeRoutingModule { }
