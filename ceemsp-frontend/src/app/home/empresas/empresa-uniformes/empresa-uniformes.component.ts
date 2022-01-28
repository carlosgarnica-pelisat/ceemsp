import {Component, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UniformeService} from "../../../_services/uniforme.service";
import {ToastType} from "../../../_enums/ToastType";
import Uniforme from "../../../_models/Uniforme";
import EmpresaUniforme from "../../../_models/EmpresaUniforme";
import EmpresaUniformeElemento from "../../../_models/EmpresaUniformeElemento";

@Component({
  selector: 'app-empresa-uniformes',
  templateUrl: './empresa-uniformes.component.html',
  styleUrls: ['./empresa-uniformes.component.css']
})
export class EmpresaUniformesComponent implements OnInit {

  uuid: string;

  private gridApi;
  private gridColumnApi;

  modal: NgbModalRef;
  closeResult: string;
  crearUniformeForm: FormGroup;
  crearUniformeElementoForm: FormGroup;

  showUniformeElementoForm: boolean = false;

  rowData;
  uniforme: EmpresaUniforme;
  uniformes: Uniforme[];
  elementoUniforme: Uniforme;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true}
  ];

  frameworkComponents: any;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaService: EmpresaService,
              private formBuilder: FormBuilder, private uniformeService: UniformeService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearUniformeForm = this.formBuilder.group({
      'nombre': ['', Validators.required],
      'descripcion': ['', Validators.required]
    });

    this.crearUniformeElementoForm = this.formBuilder.group({
      'elemento': ['', Validators.required],
      'cantidad': ['', Validators.required]
    })

    this.empresaService.obtenerUniformes(this.uuid).subscribe((data: EmpresaUniforme[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los uniformes. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.uniformeService.obtenerUniformes().subscribe((data: Uniforme[]) => {
      this.uniformes = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el catalogo de uniformes. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  seleccionarElemento(event) {
    this.elementoUniforme = this.uniformes.filter(x => x.uuid === event.value)[0];
  }

  guardarElemento(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Hay campos requeridos sin rellenar`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos guardando el elemento del uniforme`,
      ToastType.INFO
    );

    let value: EmpresaUniformeElemento = form.value;
    value.elemento = this.elementoUniforme;

    this.empresaService.guardarUniformeElemento(this.uuid, this.uniforme.uuid, value).subscribe((data: Uniforme) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el elemento con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el elemento. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarUniforme(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Hay campos requeridos sin rellenar`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos guardando el uniforme`,
      ToastType.INFO
    );

    let value: Uniforme = form.value;

    this.empresaService.guardarUniforme(this.uuid, value).subscribe((data: Uniforme) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el uniforme con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el uniforme. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarFormularioUniformeElemento() {
    this.showUniformeElementoForm = !this.showUniformeElementoForm;
  }

  mostrarModalDetalles(data, modal) {

    this.empresaService.obtenerUniformePorUuid(this.uuid, data.uuid).subscribe((data: EmpresaUniforme) => {
      this.uniforme = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar el uniforme. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
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
