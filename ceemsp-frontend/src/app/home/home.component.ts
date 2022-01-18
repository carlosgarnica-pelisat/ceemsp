import { Component, OnInit } from '@angular/core';
import { faBell, faFolder } from "@fortawesome/free-solid-svg-icons";


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  faBell = faBell;
  faFolder = faFolder;

  constructor() { }

  ngOnInit(): void {
  }

}
