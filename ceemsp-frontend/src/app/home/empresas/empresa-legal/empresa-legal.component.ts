import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import EmpresaEscritura from "../../../_models/EmpresaEscritura";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import {ToastType} from "../../../_enums/ToastType";
import EmpresaEscrituraSocio from "../../../_models/EmpresaEscrituraSocio";
import {faCheck, faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import EmpresaEscrituraApoderado from "../../../_models/EmpresaEscrituraApoderado";
import EmpresaEscrituraRepresentante from "../../../_models/EmpresaEscrituraRepresentante";
import EmpresaEscrituraConsejo from "../../../_models/EmpresaEscrituraConsejo";

@Component({
  selector: 'app-empresa-legal',
  templateUrl: './empresa-legal.component.html',
  styleUrls: ['./empresa-legal.component.css']
})
export class EmpresaLegalComponent implements OnInit {

  showSocioForm: boolean = false;
  showApoderadoForm: boolean = false;
  showRepresentanteForm: boolean = false;
  showConsejoForm: boolean = false;

  uuid: string;
  pestanaActual: string = "DETALLES";
  escrituras: EmpresaEscritura[];

  nuevaEscrituraForm: FormGroup;
  nuevoSocioForm: FormGroup;
  nuevoApoderadoForm: FormGroup;
  nuevoRepresentanteForm: FormGroup;
  nuevoConsejoAdministracionForm: FormGroup;

  modal: NgbModalRef;
  closeResult: string;
  escritura: EmpresaEscritura;

  faPencil = faPencilAlt;
  faTrash = faTrash;
  faCheck = faCheck;

  private gridApi;
  private gridColumnApi;

  columnDefs = EmpresaEscritura.obtenerColumnasPorDefault();
  allColumnDefs = EmpresaEscritura.obtenerTodasLasColumnas();
  rowData = [];

  tempFile;

  frameworkComponents: any;
  rowDataClicked = {
    uuid: undefined
  };

  @ViewChild('mostrarDetallesEscrituraModal') mostrarDetallesEscrituraModal: any;
  @ViewChild('modificarEscrituraModal') modificarEscrituraModal: any;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaService: EmpresaService,
              private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.nuevaEscrituraForm = this.formBuilder.group({
      numeroEscritura: ['', Validators.required],
      fechaEscritura: ['', Validators.required],
      ciudad: ['', Validators.required],
      tipoFedatario: ['', Validators.required],
      numero: ['', Validators.required],
      nombreFedatario: ['', Validators.required],
      descripcion: ['', Validators.required]
    })

    this.nuevoSocioForm = this.formBuilder.group({
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      sexo: ['', Validators.required]
    })

    this.nuevoApoderadoForm = this.formBuilder.group({
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      sexo: ['', Validators.required],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required]
    })

    this.nuevoRepresentanteForm = this.formBuilder.group({
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      sexo: ['', Validators.required]
    })

    this.nuevoConsejoAdministracionForm = this.formBuilder.group({
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      sexo: ['', Validators.required],
      puesto: ['', Validators.required]
    })

    this.empresaService.obtenerEscrituras(this.uuid).subscribe((data: EmpresaEscritura[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las escrituras. ${error}`,
        ToastType.ERROR
      )
    })
  }

  mostrarFormularioNuevoSocio() {
    this.showSocioForm = !this.showSocioForm;
  }

  mostrarFormularioNuevoApoderado() {
    this.showApoderadoForm = !this.showApoderadoForm;
  }

  mostrarFormularioNuevoRepresentante() {
    this.showRepresentanteForm = !this.showRepresentanteForm;
  }

  mostrarFormularioNuevoConsejo() {
    this.showConsejoForm = !this.showConsejoForm;
  }

  guardarSocio(nuevoSocioform) {

    if(!nuevoSocioform.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan campos requeridos por rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando el socio",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraSocio = nuevoSocioform.value;

    this.empresaService.guardarEscrituraSocio(this.uuid, this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraSocio) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha registrado el socio con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el socio. ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarApoderado(nuevoApoderadoForm) {

    if(!nuevoApoderadoForm.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan campos requeridos por rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el nuevo apoderado",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraApoderado = nuevoApoderadoForm.value;

    this.empresaService.guardarEscrituraApoderado(this.uuid, this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraApoderado) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha registrado el apoderado con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el apoderado. ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarConsejo(nuevoConsejoForm) {
    if(!nuevoConsejoForm.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan campos requeridos por rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el nuevo consejo",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraConsejo = nuevoConsejoForm.value;

    this.empresaService.guardarEscrituraConsejos(this.uuid, this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraRepresentante) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha registrado el consejo con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el consejo. ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarRepresentante(nuevoRepresentanteForm) {

    if(!nuevoRepresentanteForm.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan campos requeridos por rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el nuevo representante",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraRepresentante = nuevoRepresentanteForm.value;

    this.empresaService.guardarEscrituraRepresentante(this.uuid, this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraRepresentante) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha registrado el representante con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el representante. ${error}`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
  }

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
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

  modify(rowData) {

  }

  delete(rowData) {

  }

  mostrarModalDetalles(rowData, modal) {
    let escrituraUuid = rowData.uuid;
    this.empresaService.obtenerEscrituraPorUuid(this.uuid, escrituraUuid).subscribe((data: EmpresaEscritura) => {
      this.escritura = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la escritura. ${error}`,
        ToastType.ERROR
      )
    })

    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  isColumnListed(field: string ) {
    return this.columnDefs.filter(s => s.field === field)[0] !== undefined;
  }

  cambiarPestana(status) {
    if(status == this.pestanaActual) {
      return;
    }
    this.pestanaActual = status;
  }

  guardarEscritura(form) {
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
      "Estamos guardando la escritura",
      ToastType.INFO
    );

    let formValue: EmpresaEscritura = form.value;

    let formData = new FormData();
    formData.append('archivo', this.tempFile, this.tempFile.name);
    formData.append('escritura', JSON.stringify(formValue));

    this.empresaService.guardarEscritura(this.uuid, formData).subscribe((data: EmpresaEscritura) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la escritura con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la escritura. ${error}`,
        ToastType.ERROR
      );
    })
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

  mostrarModificarEscrituraModal() {
    this.nuevaEscrituraForm.setValue({
      numeroEscritura: this.escritura.numeroEscritura,
      fechaEscritura: this.escritura.fechaEscritura,
      ciudad: this.escritura.ciudad,
      tipoFedatario: this.escritura.tipoFedatario,
      numero: this.escritura.numero,
      nombreFedatario: this.escritura.nombreFedatario,
      descripcion: this.escritura.descripcion
    });

    this.modalService.dismissAll();

    this.modalService.open(this.modificarEscrituraModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  guardarCambiosEscritura(form) {
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
      "Estamos guardando los cambios en la empresa",
      ToastType.INFO
    );

    let escritura: EmpresaEscritura = form.value();

    this.empresaService.modificarEscritura(this.uuid, this.escritura.uuid, escritura).subscribe((data: EmpresaEscritura) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se han guardado los cambios de la escritura con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar la escritura. Motivo: ${error}`,
        ToastType.ERROR
      )
    });
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
