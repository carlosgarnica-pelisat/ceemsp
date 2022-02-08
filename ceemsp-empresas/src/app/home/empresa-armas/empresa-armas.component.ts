import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../_services/toast.service";
import Stepper from "bs-stepper";
import {EmpresaService} from "../../_services/empresa.service";
import {ArmasService} from "../../_services/armas.service";
import EmpresaLicenciaColectiva from "../../_models/EmpresaLicenciaColectiva";
import EmpresaDomicilio from "../../_models/EmpresaDomicilio";
import ArmaClase from "../../_models/ArmaClase";
import ArmaMarca from "../../_models/ArmaMarca";
import {ToastType} from "../../_enums/ToastType";
import Arma from "../../_models/Arma";

@Component({
  selector: 'app-empresa-armas',
  templateUrl: './empresa-armas.component.html',
  styleUrls: ['./empresa-armas.component.css']
})
export class EmpresaArmasComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Calibre', field: 'calibre', sortable: true, filter: true },
    {headerName: 'Marca', field: 'marca.nombre', sortable: true, filter: true },
    {headerName: 'Tipo', field: 'tipo', sortable: true, filter: true },
    {headerName: 'Status', field: 'status', sortable: true, filter: true},
    {headerName: 'Acciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        modify: this.modify.bind(this),
        delete: this.delete.bind(this)
      }}
  ];
  rowData = [];

  licenciasColectivas: EmpresaLicenciaColectiva[] = [];
  domicilios: EmpresaDomicilio[] = [];
  marcas: ArmaMarca[] = [];
  clases: ArmaClase[] = [];

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearArmaForm: FormGroup;

  stepper: Stepper;

  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute,
              private toastService: ToastService, private modalService: NgbModal,
              private empresaService: EmpresaService, private armaService: ArmasService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.empresaService.obtenerDomicilios(this.uuid).subscribe((data: EmpresaDomicilio[]) => {
      this.domicilios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaService.obtenerLicenciasColectivas(this.uuid).subscribe((data: EmpresaLicenciaColectiva[]) => {
      this.licenciasColectivas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las licencias colectivas. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.armaService.obtenerArmaMarcas().subscribe((data: ArmaMarca[]) => {
      this.marcas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las maracas de las armas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.armaService.obtenerArmaClases().subscribe((data: ArmaClase[]) => {
      this.clases = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las maracas de las armas. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.crearArmaForm = this.formBuilder.group({
      licenciaColectiva: ['', Validators.required],
      tipo: ['', Validators.required],
      clase: ['', Validators.required],
      marca: ['', Validators.required],
      calibre: ['', Validators.required],
      bunker: ['', Validators.required],
      status: ['']
    });
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.stepper = new Stepper(document.querySelector('#stepper1'), {
      linear: true,
      animation: true
    })

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  modify() {

  }

  delete() {

  }

  crearArma(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos requeridos sin rellenar",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el arma",
      ToastType.INFO
    );

    let formData: Arma = form.value;

    formData.bunker = this.domicilios.filter(x => x.uuid === form.value.bunker)[0];
    formData.clase = this.clases.filter(x => x.uuid === form.value.clase)[0];
    formData.marca = this.marcas.filter(x => x.uuid === form.value.marca)[0];
    formData.licenciaColectiva = this.licenciasColectivas.filter(x => x.uuid === form.value.licenciaColectiva)[0];
    formData.status = "DEPOSITO";

    this.empresaService.guardarArma(this.uuid, formData.uuid, formData).subscribe((data: Arma) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el arma con exito",
        ToastType.SUCCESS
      );

      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `no se pudo guarar el arma. Motivo: ${error}`,
        ToastType.ERROR
      );
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
