import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {EmpresaService} from "../../../_services/empresa.service";
import {ToastService} from "../../../_services/toast.service";
import {ActivatedRoute} from "@angular/router";
import {ToastType} from "../../../_enums/ToastType";
import EmpresaLicenciaColectiva from "../../../_models/EmpresaLicenciaColectiva";
import EmpresaModalidad from "../../../_models/EmpresaModalidad";
import Arma from "../../../_models/Arma";
import {faCheck, faEdit, faSync, faTrash} from "@fortawesome/free-solid-svg-icons";
import Persona from "../../../_models/Persona";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";

@Component({
  selector: 'app-empresa-licencias',
  templateUrl: './empresa-licencias.component.html',
  styleUrls: ['./empresa-licencias.component.css']
})
export class EmpresaLicenciasComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  faSync = faSync;
  faEdit = faEdit;
  faTrash = faTrash;
  faCheck = faCheck;

  columnDefs = EmpresaLicenciaColectiva.obtenerColumnasPorDefault();
  allColumnDefs = EmpresaLicenciaColectiva.obtenerTodasLasColumnas();
  rowData: EmpresaLicenciaColectiva[] = [];

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  licencia: EmpresaLicenciaColectiva;
  pestanaActual: string = "DETALLES";
  armas: Arma[];
  domicilios: EmpresaDomicilio[];
  domiciliosLicenciaColectiva: EmpresaDomicilio[];
  personal: Persona[] = [];
  status: string = "ACTIVA";

  rowDataClicked = {
    uuid: undefined
  };

  modalidades: EmpresaModalidad[];
  crearEmpresaLicenciaForm: FormGroup;
  modificarStatusArmaForm: FormGroup;
  crearDireccionForm: FormGroup;
  crearArmaForm: FormGroup;
  mostrarModificarStatusArma: boolean = false;

  showDireccionForm: boolean = false;
  showArmaForm: boolean = false;

  constructor(private modalService: NgbModal, private empresaService: EmpresaService, private toastService: ToastService,
              private route: ActivatedRoute, private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearEmpresaLicenciaForm = this.formBuilder.group({
      numeroOficio: ['', Validators.required],
      modalidad: ['', Validators.required],
      submodalidad: [''],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required]
    });

    this.modificarStatusArmaForm = this.formBuilder.group({
      status: ['', Validators.required],
      motivo: [''],
      personalAsignado: ['']
    });

    this.crearDireccionForm = this.formBuilder.group({
      direccion: ['', Validators.required]
    })

    this.crearArmaForm = this.formBuilder.group({
      tipo: ['', Validators.required],
      clase: ['', Validators.required],
      marca: ['', Validators.required],
      calibre: ['', Validators.required],
      bunker: ['', Validators.required],
      status: ['']
    })

    this.empresaService.obtenerLicenciasColectivas(this.uuid).subscribe((data: EmpresaLicenciaColectiva[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las licencias colectivas. Motivo: ${error}`,
        ToastType.ERROR
      )
    });

    this.empresaService.obtenerPersonal(this.uuid).subscribe((data: Persona[]) => {
      this.personal = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `no se han podido descargar las licencias colectivas. Motivo: ${error}`,
        ToastType.ERROR
      )
    });

    this.empresaService.obtenerDomicilios(this.uuid).subscribe((data: EmpresaDomicilio[]) => {
      this.domicilios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
  }

  cambiarPestana(pestana) {
    this.pestanaActual = pestana;
  }

  seleccionarStatus(event) {
    this.status = event.value;
  }

  seleccionarPersona(event) {
    this.status = event.value;
  }

  mostrarFormularioNuevaDireccion() {
    this.showDireccionForm = !this.showDireccionForm;
  }

  mostrarFormularioArma() {
    this.showArmaForm = !this.showArmaForm;
  }

  guardarDireccion(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han rellenado",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el domicilio en la licencia colectiva",
      ToastType.INFO
    );

    let data: EmpresaDomicilio = this.domicilios.filter(x => x.uuid === form.value.direccion)[0];

    this.empresaService.guardarDomicilioEnLicenciaColectiva(this.uuid, this.licencia.uuid, data).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el domicilio con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha guardado el domicilio en la licencia colectiva. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    });

    this.empresaService.obtenerModalidades(this.uuid).subscribe((data: EmpresaModalidad[]) => {
      this.modalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar las modalidades de la empresa. ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalDetalles(rowData, modal) {
    let licenciaUuid = rowData.uuid;
    this.modal = this.modalService.open(modal, {ariaLabelledBy: "modal-basic-title", size: 'xl'});

    this.empresaService.obtenerLicenciaColectivaPorUuid(this.uuid, licenciaUuid).subscribe((data: EmpresaLicenciaColectiva) => {
      this.licencia = data;

      this.empresaService.obtenerDomiciliosPorLicenciaColectiva(this.uuid, this.licencia.uuid).subscribe((data: EmpresaDomicilio[]) => {
        this.domiciliosLicenciaColectiva = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los domicilios de la licencia colectiva. Motivo: {error}`,
          ToastType.ERROR
        )
      })

      this.empresaService.obtenerArmasPorLicenciaColectivaUuid(this.uuid, this.licencia.uuid).subscribe((data: Arma[]) => {
        this.armas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `Las armas de la licencia colectiva no se pudieron descargar. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion de la licencia. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  modify() {

  }

  delete() {

  }

  mostrarCambioStatusForm() {
    this.mostrarModificarStatusArma = !this.mostrarModificarStatusArma;
  }

  guardarLicencia(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no estan siendo llenados",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la licencia",
      ToastType.INFO
    );

    let formValue = form.value;
    let licencia: EmpresaLicenciaColectiva = new EmpresaLicenciaColectiva();

    licencia.numeroOficio = formValue.numeroOficio;
    licencia.modalidad = this.modalidades.filter(x => x.modalidad.uuid === formValue.modalidad)[0].modalidad;
    licencia.submodalidad = this.modalidades.filter(x => x.submodalidad.uuid === formValue.submodalidad)[0].submodalidad;
    licencia.fechaInicio = formValue.fechaInicio;
    licencia.fechaFin = formValue.fechaFin;

    this.empresaService.guardarLicenciaColectiva(this.uuid, licencia).subscribe((data: EmpresaLicenciaColectiva) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la licencia con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la licencia. ${error}`,
        ToastType.ERROR
      );
    });
  }

  exportGridData(format) {
    switch(format) {
      case "CSV":
        this.gridApi.exportDataAsCsv();
        break;
      case "PDF":
        this.toastService.showGenericToast(
          "Bajo desarrollo",
          "Actualmente estamos desarrollando esta funcionalidad",
          ToastType.INFO
        )
        break;
      default:
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "No podemos exportar en dicho formato",
          ToastType.WARNING
        )
        break;
    }
  }

  toggleColumn(field: string) {
    let columnDefinitionIndex = this.columnDefs.findIndex(s => s.field === field);
    if(columnDefinitionIndex === -1) {
      let columnDefinition = this.allColumnDefs.filter(s => s.field === field)[0];

      let newColumnDef = {
        headerName: columnDefinition.headerName,
        field: columnDefinition.field,
        sortable: true,
        filter: true
      };

      this.columnDefs.push(newColumnDef);
      this.gridApi.setColumnDefs(this.columnDefs);
    } else {
      this.columnDefs = this.columnDefs.filter(s => s.field !== field);
    }
  }

  isColumnListed(field: string ) {
    return this.columnDefs.filter(s => s.field === field)[0] !== undefined;
  }

  mostrarModificarLicenciaModal() {

  }

  mostrarEliminarLicenciaModal() {

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
