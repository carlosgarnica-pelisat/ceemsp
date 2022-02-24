import {Component, OnInit, ViewChild} from '@angular/core';
import {ToastService} from "../../../_services/toast.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {EmpresaService} from "../../../_services/empresa.service";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import {ActivatedRoute} from "@angular/router";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ToastType} from "../../../_enums/ToastType";
import {faCheck} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-empresa-domicilios',
  templateUrl: './empresa-domicilios.component.html',
  styleUrls: ['./empresa-domicilios.component.css']
})
export class EmpresaDomiciliosComponent implements OnInit {

  faCheck = faCheck;

  uuid: string;
  domicilios: EmpresaDomicilio[];

  nuevoDomicilioForm: FormGroup;
  modificarDomicilioForm: FormGroup;
  modal: NgbModalRef;
  closeResult: string;

  private gridApi;
  private gridColumnApi;

  domicilio: EmpresaDomicilio;

  columnDefs = EmpresaDomicilio.obtenerColumnasPorDefault();
  allColumnDefs = EmpresaDomicilio.obtenerTodasLasColumnas();

  rowData = [];

  frameworkComponents: any;
  rowDataClicked = {
    uuid: undefined
  };

  @ViewChild('mostrarDetallesDomicilioModal') mostrarDetallesDomicilioModal: any;
  @ViewChild('modificarDomicilioModal') modificarDomicilioModal: any;
  @ViewChild('eliminarDomicilioModal') eliminarDomicilioModal: any;

  constructor(private toastService: ToastService, private formbuilder: FormBuilder,
              private empresaService: EmpresaService, private route: ActivatedRoute,
              private modalService: NgbModal) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.modificarDomicilioForm = this.formbuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      domicilio1: ['', [Validators.required, Validators.maxLength(100)]],
      numeroExterior: ['', [Validators.required, Validators.maxLength(20)]],
      numeroInterior: ['', [Validators.maxLength(20)]],
      domicilio2: ['', [Validators.required, Validators.maxLength(100)]],
      domicilio3: ['', [Validators.required, Validators.maxLength(100)]],
      domicilio4: ['', [Validators.maxLength]],
      codigoPostal: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(5)]],
      estado: ['', Validators.required],
      pais: ['', [Validators.required, Validators.maxLength(100)]],
      telefonoFijo: ['', [Validators.required]],
      telefonoMovil: ['', [Validators.required]]
      // TODO: Volver a agregar los campos latitud y longitud cuando se tenga la extension de google maps
    });

    this.nuevoDomicilioForm = this.formbuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      domicilio1: ['', [Validators.required, Validators.maxLength(100)]],
      numeroExterior: ['', [Validators.required, Validators.maxLength(20)]],
      numeroInterior: ['', [Validators.maxLength(20)]],
      domicilio2: ['', Validators.required],
      domicilio3: ['', Validators.required],
      domicilio4: [''],
      codigoPostal: ['', Validators.required],
      estado: ['', Validators.required],
      pais: ['Mexico', Validators.required],
      matriz: ['', Validators.required], // TODO: Quitar el si/no y agregar tipo de domicilio como matriz / sucursal
      telefonoFijo: ['', [Validators.required]],
      telefonoMovil: ['', [Validators.required]]
      // TODO: Volver a agregar los campos latitud y longitud cuando se tenga la extension de google maps
    })

    this.empresaService.obtenerDomicilios(this.uuid).subscribe((data: EmpresaDomicilio[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  mostrarDetalles(rowData, modal) {
    let uuid = rowData.uuid;
    this.empresaService.obtenerDomicilioPorUuid(this.uuid, uuid).subscribe((data: EmpresaDomicilio) => {
      this.domicilio = data;
      this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});
      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el domicilio. Motivo: ${error}`,
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
    })
  }

  modify(rowData) {

  }

  delete(rowData) {

  }

  isColumnListed(field: string ) {
    return this.columnDefs.filter(s => s.field === field)[0] !== undefined;
  }

  modificarDomicilio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos sin rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando los cambios en el domicilio",
      ToastType.INFO
    );

    let domicilio: EmpresaDomicilio = form.value;

    this.empresaService.modificarDomicilio(this.uuid, this.domicilio.uuid, domicilio).subscribe((response) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado el domicilio con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar el domicilio. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
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

  guardarDomicilio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay camops del formulario requeridos que no tienen informacion",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando el nuevo domicilio",
      ToastType.INFO
    );

    let domicilio: EmpresaDomicilio = form.value;

    this.empresaService.guardarDomicilio(this.uuid, domicilio).subscribe((data: EmpresaDomicilio) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la el domicilio con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la empresa. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  mostrarModificarDomicilioModal() {
    this.modificarDomicilioForm.setValue({
      nombre: this.domicilio.nombre,
      domicilio1: this.domicilio.domicilio1,
      numeroExterior: this.domicilio.numeroExterior,
      numeroInterior: this.domicilio.numeroInterior,
      domicilio2: this.domicilio.domicilio2,
      domicilio3: this.domicilio.domicilio3,
      domicilio4: this.domicilio.domicilio4,
      codigoPostal: this.domicilio.codigoPostal,
      estado: this.domicilio.estado,
      pais: this.domicilio.pais,
      telefonoFijo: this.domicilio.telefonoFijo,
      telefonoMovil: this.domicilio.telefonoMovil
    })

    this.modalService.dismissAll();

    this.modalService.open(this.modificarDomicilioModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarEliminarEmpresaModal() {
    this.modalService.dismissAll();

    this.modalService.open(this.eliminarDomicilioModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  confirmarEliminarDomicilio() {
    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos eliminando el domicilio",
      ToastType.INFO
    );

    this.empresaService.eliminarDomicilio(this.uuid, this.domicilio.uuid).subscribe((data: EmpresaDomicilio) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el domicilio con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido elimimar el domicilio. Motivo: ${error}`,
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
