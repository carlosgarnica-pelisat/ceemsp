import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {EmpresaService} from "../../../_services/empresa.service";
import {ToastService} from "../../../_services/toast.service";
import {ActivatedRoute} from "@angular/router";
import {ToastType} from "../../../_enums/ToastType";
import EmpresaLicenciaColectiva from "../../../_models/EmpresaLicenciaColectiva";
import EmpresaModalidad from "../../../_models/EmpresaModalidad";
import Arma from "../../../_models/Arma";
import {faCheck, faEdit, faSync, faTrash, faPencilAlt} from "@fortawesome/free-solid-svg-icons";
import Persona from "../../../_models/Persona";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import ArmaMarca from "../../../_models/ArmaMarca";
import ArmaClase from "../../../_models/ArmaClase";
import {ArmasService} from "../../../_services/armas.service";

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
  faPencil = faPencilAlt;

  marcas: ArmaMarca[] = [];
  clases: ArmaClase[] = [];

  tempFile;

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

  pdfActual;

  modalidades: EmpresaModalidad[];
  crearEmpresaLicenciaForm: FormGroup;
  modificarStatusArmaForm: FormGroup;
  crearDireccionForm: FormGroup;
  crearArmaForm: FormGroup;
  mostrarModificarStatusArma: boolean = false;

  showDireccionForm: boolean = false;
  showArmaForm: boolean = false;

  tempUuidDireccion: string;
  tempUuidArma: string;

  arma: Arma;
  editandoArma: boolean;

  @ViewChild('modificarLicenciaModal') modificarLicenciaModal;

  @ViewChild('eliminarEmpresaLicenciaModal') eliminarEmpresaLicenciaModal;
  @ViewChild('eliminarEmpresaLicenciaDireccionModal') eliminarEmpresaLicenciaDireccionModal;
  @ViewChild('eliminarEmpresaLicenciaArmaModal') eliminarEmpresaLicenciaArmaModal;

  constructor(private modalService: NgbModal, private empresaService: EmpresaService, private toastService: ToastService,
              private route: ActivatedRoute, private formBuilder: FormBuilder, private armaService: ArmasService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearEmpresaLicenciaForm = this.formBuilder.group({
      numeroOficio: ['', Validators.required],
      modalidad: ['', Validators.required],
      submodalidad: [''],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required],
      archivo: ['', Validators.required]
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
    if(!this.showArmaForm) {
      this.crearArmaForm.reset();
    }
    if(this.editandoArma) {
      this.editandoArma = false;
      this.arma = undefined;
    }
  }

  convertirPdf(pdf: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.pdfActual = reader.result;
    });

    if(pdf) {
      reader.readAsDataURL(pdf);
    }
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

    let data: EmpresaDomicilio = this.domicilios.filter(x => x.uuid === form.value.direccion)[0];
    let existeDomicilio = this.domiciliosLicenciaColectiva.filter(x => x.uuid === form.value.direccion)[0];

    if(existeDomicilio !== undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El domicilio ya se encuentra registrado en la licencia colectiva`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el domicilio en la licencia colectiva",
      ToastType.INFO
    );

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

  modificarLicencia(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El formulario es invalido`,
        ToastType.WARNING
      );
      return;
    }

    let formValue = form.value;
    let licencia: EmpresaLicenciaColectiva = new EmpresaLicenciaColectiva();

    licencia.numeroOficio = formValue.numeroOficio;
    licencia.modalidad = this.modalidades.filter(x => x.modalidad.uuid === formValue.modalidad)[0].modalidad;
    //licencia.submodalidad = this.modalidades.filter(x => x.submodalidad.uuid === formValue.submodalidad)[0].submodalidad; //TODO: revisar por que esta fallando esta mamada
    licencia.fechaInicio = formValue.fechaInicio;
    licencia.fechaFin = formValue.fechaFin;

    let fechaInicio = new Date(licencia.fechaInicio);
    let fechaFin = new Date(licencia.fechaFin);
    if(fechaInicio > fechaFin) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "La fecha de inicio es mayor que la del final",
        ToastType.WARNING
      )
      return;
    }
    let existeModalidad = this.rowData.filter(x => x.modalidad.uuid === licencia.modalidad.uuid)[0];
    if(existeModalidad !== undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "La modalidad ya se encuentra registrada en esta licencia colectiva",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos modificando la licencia",
      ToastType.INFO
    );

    let formData = new FormData();
    formData.append('licencia', JSON.stringify(licencia));
    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null);
    }

    this.empresaService.modificarLicenciaColectiva(this.uuid, this.licencia.uuid, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha actualizado la licencia colectiva con exito`,
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar la licencia colectiva. Motivo: ${error}`,
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

  onFileChange(event) {
    this.tempFile = event.target.files[0]
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

    let formValue = form.value;
    let licencia: EmpresaLicenciaColectiva = new EmpresaLicenciaColectiva();

    licencia.numeroOficio = formValue.numeroOficio;
    licencia.modalidad = this.modalidades.filter(x => x.modalidad.uuid === formValue.modalidad)[0].modalidad;
    //licencia.submodalidad = this.modalidades.filter(x => x.submodalidad.uuid === formValue.submodalidad)[0].submodalidad; //TODO: revisar por que esta fallando esta mamada
    licencia.fechaInicio = formValue.fechaInicio;
    licencia.fechaFin = formValue.fechaFin;

    let fechaInicio = new Date(licencia.fechaInicio);
    let fechaFin = new Date(licencia.fechaFin);
    if(fechaInicio > fechaFin) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "La fecha de inicio es mayor que la del final",
        ToastType.WARNING
      )
      return;
    }
    let existeModalidad = this.rowData.filter(x => x.modalidad.uuid === licencia.modalidad.uuid)[0];
    if(existeModalidad !== undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "La modalidad ya se encuentra registrada en esta licencia colectiva",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la licencia",
      ToastType.INFO
    );

    let formData = new FormData();
    formData.append('archivo', this.tempFile, this.tempFile.name);
    formData.append('licencia', JSON.stringify(licencia));

    this.empresaService.guardarLicenciaColectiva(this.uuid, formData).subscribe((data: EmpresaLicenciaColectiva) => {
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

  descargarLicencia(uuid, modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.empresaService.descargarLicenciaPdf(this.uuid, this.licencia.uuid).subscribe((data: Blob) => {
      this.convertirPdf(data);
      // TODO: Manejar esta opcion para descargar
      /*let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "licencia-colectiva-" + this.licencia.uuid;
      link.click();*/
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el PDF. Motivo: ${error}`,
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

  mostrarModificarArmaForm(index) {
    this.arma = this.armas[index];
    this.mostrarFormularioArma();
    this.editandoArma = true;
    this.crearArmaForm.patchValue({
      tipo: this.arma.tipo,
      clase: this.arma.clase.uuid,
      marca: this.arma.marca.uuid,
      calibre: this.arma.calibre,
      bunker: this.arma.bunker.uuid
    });
  }

  mostrarModificarLicenciaModal() {
    this.crearEmpresaLicenciaForm.patchValue({
      numeroOficio: this.licencia.numeroOficio,
      modalidad: this.licencia.modalidad.uuid,
      submodalidad: this.licencia.submodalidad.uuid,
      fechaInicio: this.licencia.fechaInicio,
      fechaFin: this.licencia.fechaFin
    });
    this.crearEmpresaLicenciaForm.controls['archivo'].clearValidators();
    this.crearEmpresaLicenciaForm.controls['archivo'].updateValueAndValidity();

    this.modal = this.modalService.open(this.modificarLicenciaModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarEliminarLicenciaModal() {
    this.modal = this.modalService.open(this.eliminarEmpresaLicenciaModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarFormularioNuevaArma() {
    this.showArmaForm = !this.showArmaForm;
  }

  mostrarModalEliminarDomicilio(uuid) {
    this.tempUuidDireccion = uuid;
    this.modal = this.modalService.open(this.eliminarEmpresaLicenciaDireccionModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarArma(uuid) {
    this.tempUuidArma = uuid;
    this.modal = this.modalService.open(this.eliminarEmpresaLicenciaArmaModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
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
    formData.status = "DEPOSITO";

    console.log(formData);

    if(this.editandoArma) {
      this.empresaService.modificarArma(this.uuid, this.licencia.uuid, this.arma.uuid, formData).subscribe((data) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el arma con exito",
          ToastType.SUCCESS
        );
        window.location.reload();
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el arma. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
    } else {
      this.empresaService.guardarArma(this.uuid, this.licencia.uuid, formData).subscribe((data: Arma) => {
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
  }

  confirmarEliminarLicencia() {
    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la licencia colectiva",
      ToastType.INFO
    );

    this.empresaService.eliminarLicenciaColectiva(this.uuid, this.licencia.uuid).subscribe((data: EmpresaLicenciaColectiva) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la licencia colectiva con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la licencia colectiva. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarLicenciaArma() {
    if(this.tempUuidArma === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del domicilio a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el arma de la licencia colectiva",
      ToastType.INFO
    );

    this.empresaService.eliminarArma(this.uuid, this.licencia.uuid, this.tempUuidArma).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el arma de la licencia colectiva con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El arma de la licencia colectiva no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarLicenciaDomicilio() {
    if(this.tempUuidDireccion === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del domicilio a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el domicilio de la licencia colectiva",
      ToastType.INFO
    );

    this.empresaService.eliminarDomicilioEnLicenciaColectiva(this.uuid, this.licencia.uuid, this.tempUuidDireccion).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el domicilio de la licencia colectiva con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El domicilio de la licencia colectiva no se ha podido eliminar. Motivo: ${error}`,
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
