import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import Usuario from "../../../_models/Usuario";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastService} from "../../../_services/toast.service";
import {UsuariosService} from "../../../_services/usuarios.service";
import {Router} from "@angular/router";
import {ToastType} from "../../../_enums/ToastType";
import * as sha256 from "js-sha256";

@Component({
  selector: 'app-usuarios-empresa',
  templateUrl: './usuarios-empresa.component.html',
  styleUrls: ['./usuarios-empresa.component.css']
})
export class UsuariosEmpresaComponent implements OnInit {

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Registro', field: 'empresa.registro', sortable: true, filter: true },
    {headerName: 'Razon Social', field: 'empresa.razonSocial', sortable: true, filter: true },
    {headerName: 'Nombre comercial', field: 'empresa.nombreComercial', sortable: true, filter: true},
    {headerName: 'Rol', field: 'rol', sortable: true, filter: true}
  ];

  movimientos = [];

  pestanaActual = 'DETALLES';
  rowData = [];

  private gridApi;
  private gridColumnApi;

  frameworkComponents: any;

  closeResult: string;
  modal: NgbModalRef;
  usuario: Usuario;

  @ViewChild("detallesUsuarioModal") detallesUsuarioModal;

  constructor(private toastService: ToastService, private modalService: NgbModal, private usuarioService: UsuariosService,
              private formBuilder: FormBuilder, private router: Router) { }

  ngOnInit(): void {
    this.usuarioService.obtenerUsuariosEmpresas().subscribe((data: Usuario[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los usuarios. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  checkForDetails(data) {
    let uuid = data.uuid;

    this.usuarioService.obtenerUsuarioByUuid(uuid).subscribe((data: Usuario) => {
      this.usuario = data;

      this.modal = this.modalService.open(this.detallesUsuarioModal, {size: "xl"});

      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el usuario. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  redireccionarVerDetallesEmpresa() {
    this.modal.close();
    this.router.navigate([`/home/empresas/${this.usuario?.empresa?.uuid}`]);
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
