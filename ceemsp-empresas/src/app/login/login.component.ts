import { Component, OnInit } from '@angular/core';
import {EmailValidator, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthenticationService} from "../_services/authentication.service";
import {first} from "rxjs/operators";
import Credential from "../_models/Credential";
import {ComunicadosGeneralesService} from "../_services/comunicados-generales.service";
import ComunicadoGeneral from "../_models/ComunicadoGeneral";
import {PublicService} from "../_services/public.service";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup | undefined;
  loading = false;
  submitted = false;
  returnUrl: string | undefined;
  comunicadoGeneral: ComunicadoGeneral;
  comunicadosGenerales: ComunicadoGeneral[];

  modal: NgbModalRef;
  closeResult: string;

  comunicadoUuid: string;
  comunicado: ComunicadoGeneral;

  private credential: Credential = new Credential();

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authenticationService: AuthenticationService,
    private comunicadosGeneralesService: ComunicadosGeneralesService,
    private publicService: PublicService,
    private modalService: NgbModal
  ) {
    if(this.authenticationService.currentUserValue) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      email: ['', Validators.required],
      password: ['', Validators.required]
    });

    this.obtenerComunicadosGenerales()

    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  obtenerComunicadosGenerales() {
    this.publicService.obtenerUltimoComunicado().subscribe((data: ComunicadoGeneral) => {
      this.comunicadoGeneral = data
    }, (error) => {
      console.error(error)
    })
  }

  mostrarHistorialComunicados(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  get f() {
    return this.loginForm.controls;
  }

  login() {
    this.submitted = true;

    if(this.loginForm.invalid) {
      return;
    }

    this.loading = true;

    this.credential = new Credential();

    this.credential.email = this.f.email.value;
    this.credential.password = this.f.password.value;

    this.authenticationService.login(this.credential)
      .pipe(first())
      .subscribe((data) => {
        this.router.navigate([this.returnUrl]);
      }, (error) => {
        this.loading = false;
      })
  }

  verHistorialComunicados(modal) {
    this.publicService.obtenerComunicados().subscribe((data: ComunicadoGeneral[]) => {
      this.comunicadosGenerales = data;
      this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      })
    }, (error) => {
      console.log(error);
    });
  }

  obtenerComunicado(uuid) {
    this.comunicadoUuid = uuid;

    this.publicService.obtenerComunicadoPorUuid(this.comunicadoUuid).subscribe((data: ComunicadoGeneral) => {
      this.comunicado = data;
    }, (error) => {
      console.error(error)
    })
  }

  private getDismissReason(reason: any): string {
    if (reason == ModalDismissReasons.ESC) {
      return `by pressing ESC`;
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return `by clicking on a backdrop`;
    } else {
      return `with ${reason}`;
    }
  }

}
