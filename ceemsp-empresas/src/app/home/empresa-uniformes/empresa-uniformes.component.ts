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
import {faPencilAlt, faTrash, faBook, faDownload} from "@fortawesome/free-solid-svg-icons";
import EmpresaUniformeElemento from "../../_models/EmpresaUniformeElemento";
import {EmpresaUniformeService} from "../../_services/empresa-uniforme.service";
import EmpresaUniformeElementoMovimiento from "../../_models/EmpresaUniformeElementoMovimiento";
import {
  BotonEmpresaUniformeComponent
} from "../../_components/botones/boton-empresa-uniforme/boton-empresa-uniforme.component";

@Component({
  selector: 'app-empresa-uniformes',
  templateUrl: './empresa-uniformes.component.html',
  styleUrls: ['./empresa-uniformes.component.css']
})
export class EmpresaUniformesComponent implements OnInit {

  uuid: string;

  private gridApi;
  private gridColumnApi;

  tempFile;
  imagenActual;

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

  altas: number = 0;
  bajas: number = 0;
  cantidadActual: number = 0;

  pestanaActual: string = "DETALLES";

  imagenPrincipal: any;

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Descripcion', field: 'descripcion', sortable: true, filter: true},
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        editar: this.editar.bind(this),
        eliminar: this.eliminar.bind(this)
      }}
  ];

  faPencil = faPencilAlt;
  faTrash = faTrash;
  faDownload = faDownload;
  faBook = faBook;

  imagenUniforme;

  empresaUniformeElemento: EmpresaUniformeElemento;

  frameworkComponents: any;

  @ViewChild('mostrarDetallesUniformeModal') mostrarDetallesUniformeModal;
  @ViewChild("modificarUniformeModal") modificarUniformeModal;
  @ViewChild("eliminarUniformeModal") eliminarUniformeModal;
  @ViewChild('eliminarUniformeElementoModal') eliminarUniformeElementoModal;
  @ViewChild('mostrarElementoModal') mostrarElementoModal;
  @ViewChild('mostrarMovimientosModal') mostrarMovimientosModal;
  @ViewChild('mostrarUniformeCompletoModal') mostrarUniformeCompletoModal;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaUniformeService: EmpresaUniformeService,
              private formBuilder: FormBuilder, private uniformeService: UniformeService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaUniformeComponent
    }

    this.uuid = this.route.snapshot.paramMap.get("uuid");
    this.crearUniformeForm = this.formBuilder.group({
      'nombre': ['', [Validators.required, Validators.minLength(0), Validators.maxLength(100)]],
      'descripcion': ['', [Validators.required, Validators.minLength(0), Validators.maxLength(300)]]
    });

    this.crearUniformeElementoForm = this.formBuilder.group({
      'elemento': ['', Validators.required],
      'altas': ['', [Validators.required, Validators.min(0), Validators.max(999)]],
      'bajas': ['', [Validators.required, Validators.min(0), Validators.max(999)]],
      'cantidadActual': ['', [Validators.required, Validators.min(0), Validators.max(999)]]
    })

    this.crearUniformeElementoForm.controls['cantidadActual'].disable()

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

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData, this.mostrarDetallesUniformeModal);
  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
  }

  editar(rowData) {
    this.empresaUniformeService.obtenerUniformePorUuid(rowData.rowData?.uuid).subscribe((data: EmpresaUniforme) => {
      this.uniforme = data;
      this.mostrarModalModificarUniforme();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el uniforme. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  eliminar(rowData) {
    this.empresaUniformeService.obtenerUniformePorUuid(rowData.rowData?.uuid).subscribe((data: EmpresaUniforme) => {
      this.uniforme = data;
      this.mostrarModalEliminarUniforme();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el uniforme. Motivo: ${error}`,
        ToastType.ERROR
      )
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

  mostrarMovimientos(index) {
    this.empresaUniformeElemento = this.uniforme.elementos[index];
    this.modal = this.modalService.open(this.mostrarMovimientosModal, {size: "lg"})
  }

  descargarFotografiaUniformeElemento(uuid) {
    this.empresaUniformeService.descargarFotografiaUniformeElemento(this.uniforme.uuid, uuid).subscribe((data: Blob) => {
      this.convertirImagen(data);
      this.modalService.open(this.mostrarElementoModal, {size: 'lg'});
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el elemento del uniforme. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  convertirImagen(imagen: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.imagenActual = reader.result;
    });

    if(imagen) {
      reader.readAsDataURL(imagen);
    }
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

    let existeElemento: EmpresaUniformeElemento = this.uniforme.elementos.filter(x => x.elemento?.uuid === this.elementoUniforme?.uuid && x?.uuid !== this.empresaUniformeElemento?.uuid)[0];

    if(existeElemento !== undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El elemento del uniforme ya se encuentra registrado`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos guardando el elemento del uniforme`,
      ToastType.INFO
    );

    let empresaUniformeElemento: EmpresaUniformeElemento = new EmpresaUniformeElemento();
    let value = form.value;
    let movimiento: EmpresaUniformeElementoMovimiento = new EmpresaUniformeElementoMovimiento();

    movimiento.altas = value.altas;
    movimiento.bajas = form.controls['bajas'].value;
    movimiento.cantidadActual = form.controls['cantidadActual'].value;

    empresaUniformeElemento.cantidad = movimiento.cantidadActual;
    empresaUniformeElemento.elemento = this.elementoUniforme;
    empresaUniformeElemento.movimientos = [];

    empresaUniformeElemento.movimientos.push(movimiento);
    let formData: FormData = new FormData();
    formData.append('elemento', JSON.stringify(empresaUniformeElemento));

    if(this.editandoElemento) {
      if(this.tempFile !== undefined) {
        formData.append('archivo', this.tempFile, this.tempFile.name);
      } else {
        formData.append('archivo', null);
      }

      this.empresaUniformeService.modificarUniformeElemento(this.uniforme.uuid, this.empresaUniformeElemento.uuid, formData).subscribe((data: EmpresaUniformeElemento) => {
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
      formData.append('archivo', this.tempFile, this.tempFile.name);
      this.empresaUniformeService.guardarUniformeElemento(this.uniforme.uuid, formData).subscribe((data: Uniforme) => {
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
    this.crearUniformeElementoForm.controls['bajas'].enable()
    this.crearUniformeElementoForm.patchValue({
      elemento: this.empresaUniformeElemento.elemento.uuid,
      cantidadActual: this.empresaUniformeElemento.cantidad
    });
    this.elementoUniforme = this.uniformes.filter(x => x.uuid === this.empresaUniformeElemento.elemento.uuid)[0];
    this.cantidadActual = this.empresaUniformeElemento.cantidad;
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

    if(this.tempFile === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Favor de subir un archivo`,
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
    let formData: FormData = new FormData();
    formData.append('uniforme', JSON.stringify(value));
    formData.append('archivo', this.tempFile, this.tempFile.name);

    this.empresaUniformeService.guardarUniforme(formData).subscribe((data: Uniforme) => {
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
      this.empresaUniformeService.descargarFotografiaUniforme(data.uuid).subscribe((data: Blob) => {
        this.convertirFotoPrincipal(data);
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la fotografia. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
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

  convertirFotoPrincipal(imagen: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.imagenPrincipal = reader.result;
    });

    if(imagen) {
      reader.readAsDataURL(imagen);
    }
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

  actualizarAltas(event) {
    let altas = event.value;
    if(altas < 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Las altas no pueden ser menores a 0`,
        ToastType.WARNING
      );
      return;
    }

    this.altas = altas;

    this.crearUniformeElementoForm.patchValue({
      cantidadActual: (+this.cantidadActual) + (+this.altas - this.bajas)
    })
  }

  actualizarBajas(event) {
    let bajas = event.value;
    if(bajas < 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Las bajas no pueden ser menores a 0`,
        ToastType.WARNING
      );
      return;
    }

    this.bajas = bajas;

    this.crearUniformeElementoForm.patchValue({
      cantidadActual: this.cantidadActual + (this.altas - this.bajas)
    })
  }

  cerrarModalEditarUniforme() {
    this.crearUniformeForm.reset();
    this.modal.close();
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
