import { Component, OnInit } from '@angular/core';
import { faBell, faFolder, faSignOutAlt } from "@fortawesome/free-solid-svg-icons";
import {AuthenticationService} from "../_services/authentication.service";
import {Router} from "@angular/router";


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  faBell = faBell;
  faFolder = faFolder;
  faSignOutAlt = faSignOutAlt;

  constructor(private authenticationService: AuthenticationService, private router: Router) { }

  ngOnInit(): void {
  }

  logout() {
    console.log("Holi, ando en el frontend");
    this.authenticationService.doLogout()
      .subscribe(() => {
        this.router.navigate(['/login']);
      });
  }

}
