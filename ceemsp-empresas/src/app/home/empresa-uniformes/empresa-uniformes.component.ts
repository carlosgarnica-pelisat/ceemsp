import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../_services/toast.service";
import {EmpresaService} from "../../_services/empresa.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UniformeService} from "../../_services/uniforme.service";
import {ToastType} from "../../_enums/ToastType";
import Uniforme from "../../_models/Uniforme";
import EmpresaUniforme from "../../_models/EmpresaUniforme";
import {faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import EmpresaUniformeElemento from "../../_models/EmpresaUniformeElemento";
import {EmpresaUniformeService} from "../../_services/empresa-uniforme.service";

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
  tempUuidElemento: string;
  crearUniformeForm: FormGroup;
  crearUniformeElementoForm: FormGroup;

  showUniformeElementoForm: boolean = false;

  rowData;
  uniforme: EmpresaUniforme;
  uniformes: Uniforme[];
  elementoUniforme: Uniforme;
  editandoElemento: boolean = false;

  pestanaActual: string = "DETALLES";

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true}
  ];

  faPencil = faPencilAlt;
  faTrash = faTrash;

  empresaUniformeElemento: EmpresaUniformeElemento;

  frameworkComponents: any;

  @ViewChild("modificarUniformeModal") modificarUniformeModal;
  @ViewChild("eliminarUniformeModal") eliminarUniformeModal;
  @ViewChild('eliminarUniformeElementoModal') eliminarUniformeElementoModal;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaUniformeService: EmpresaUniformeService,
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

    this.empresaUniformeService.obtenerUniformes().subscribe((data: EmpresaUniforme[]) => {
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

    if(this.editandoElemento) {

      this.empresaUniformeService.modificarUniformeElemento(this.uniforme.uuid, this.empresaUniformeElemento.uuid, value).subscribe((data: EmpresaUniformeElemento) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el elemento con exito",
          ToastType.SUCCESS
        );
        this.empresaUniformeService.obtenerUniformePorUuid(this.uniforme.uuid).subscribe((data: EmpresaUniforme) => {
          this.uniforme = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se pudo descargar el uniforme. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el elemento. ${error}`,
          ToastType.ERROR
        );
      });

    } else {
      this.empresaUniformeService.guardarUniformeElemento(this.uniforme.uuid, value).subscribe((data: Uniforme) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado el elemento con exito",
          ToastType.SUCCESS
        );
        this.empresaUniformeService.obtenerUniformePorUuid(this.uniforme.uuid).subscribe((data: EmpresaUniforme) => {
          this.uniforme = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se pudo descargar el uniforme. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el elemento. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  cambiarPestana(status) {
    if(status == this.pestanaActual) {
      return;
    }
    this.pestanaActual = status;
  }

  mostrarEditarElemento(index) {
    this.empresaUniformeElemento = this.uniforme.elementos[index];
    this.mostrarFormularioUniformeElemento();
    this.editandoElemento = true;
    this.crearUniformeElementoForm.patchValue({
      elemento: this.empresaUniformeElemento.elemento.uuid,
      cantidad: this.empresaUniformeElemento.cantidad
    });
    this.elementoUniforme = this.uniformes.filter(x => x.uuid === this.empresaUniformeElemento.uuid)[0];
  }

  mostrarModalEliminarElemento(tempUuid) {
    this.tempUuidElemento = tempUuid;
    this.modal = this.modalService.open(this.eliminarUniformeElementoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalModificarUniforme() {
    this.crearUniformeForm.setValue({
      nombre: this.uniforme.nombre,
      descripcion: this.uniforme.descripcion
    });

    this.modalService.open(this.modificarUniformeModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarModalEliminarUniforme() {
    this.modalService.open(this.eliminarUniformeModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  confirmarEliminarUniformeElemento() {
    if(this.tempUuidElemento === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del elemento a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el elemento",
      ToastType.INFO
    );

    this.empresaUniformeService.eliminarUniformeElemento(this.uniforme.uuid, this.tempUuidElemento).subscribe((data: EmpresaUniformeElemento) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el elemento con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaUniformeService.obtenerUniformePorUuid(this.uniforme.uuid).subscribe((data: EmpresaUniforme) => {
        this.uniforme = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se pudo descargar el uniforme. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El elemento no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarUniforme() {
    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el uniforme",
      ToastType.INFO
    );

    this.empresaUniformeService.eliminarUniforme(this.uniforme.uuid).subscribe((data: EmpresaUniforme) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el uniforme con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar el uniforme. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  guardarCambiosUniforme(form) {
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
      `Estamos guardando los cambios en el uniforme`,
      ToastType.INFO
    );

    let value: EmpresaUniforme = form.value;

    this.empresaUniformeService.modificarUniforme(this.uniforme.uuid, value).subscribe((data: Uniforme) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado el uniforme con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificado el uniforme. Motivo: ${error}`,
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

    let value: EmpresaUniforme = form.value;

    this.empresaUniformeService.guardarUniforme(value).subscribe((data: Uniforme) => {
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
    if(!this.showUniformeElementoForm) {
      this.crearUniformeElementoForm.reset();
    }
    if(this.editandoElemento) {
      this.editandoElemento = false;
      this.empresaUniformeElemento = undefined;
    }
  }

  mostrarModalDetalles(data, modal) {

    this.empresaUniformeService.obtenerUniformePorUuid(data.uuid).subscribe((data: EmpresaUniforme) => {
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
