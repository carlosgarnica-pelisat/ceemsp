import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastService} from "../../_services/toast.service";
import {ActivatedRoute} from "@angular/router";
import {ToastType} from "../../_enums/ToastType";
import EmpresaLicenciaColectiva from "../../_models/EmpresaLicenciaColectiva";
import EmpresaModalidad from "../../_models/EmpresaModalidad";
import Arma from "../../_models/Arma";
import {faCheck, faPencilAlt, faSync, faHandPaper, faTrash} from "@fortawesome/free-solid-svg-icons";
import Persona from "../../_models/Persona";
import EmpresaDomicilio from "../../_models/EmpresaDomicilio";
import ArmaMarca from "../../_models/ArmaMarca";
import ArmaClase from "../../_models/ArmaClase";
import {EmpresaLicenciasService} from "../../_services/empresa-licencias.service";
import {EmpresaPersonalService} from "../../_services/empresa-personal.service";
import {DomiciliosService} from "../../_services/domicilios.service";
import {ArmasService} from "../../_services/armas.service";
import {EmpresaModalidadesService} from "../../_services/empresa-modalidades.service";

@Component({
  selector: 'app-empresa-licencias',
  templateUrl: './empresa-licencias.component.html',
  styleUrls: ['./empresa-licencias.component.css']
})
export class EmpresaLicenciasComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  faSync = faSync;
  faPencil = faPencilAlt;
  faTrash = faTrash;
  faCheck = faCheck;
  faHandPaper = faHandPaper;

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

  model = {
    editorData: '<p>Escribe con detalle el relato de los hechos. Toma en cuenta que al finalizar se creara una incidencia de manera automatica y el arma quedara EN CUSTODIA.</p>'
  }

  @ViewChild('modificarLicenciaModal') modificarLicenciaModal;

  @ViewChild('eliminarEmpresaLicenciaModal') eliminarEmpresaLicenciaModal;
  @ViewChild('eliminarEmpresaLicenciaDireccionModal') eliminarEmpresaLicenciaDireccionModal;
  @ViewChild('eliminarEmpresaLicenciaArmaModal') eliminarEmpresaLicenciaArmaModal;

  constructor(private modalService: NgbModal, private empresaLicenciaService: EmpresaLicenciasService, private toastService: ToastService,
              private route: ActivatedRoute, private formBuilder: FormBuilder, private empresaPersonalService: EmpresaPersonalService,
              private domiciliosService: DomiciliosService, private armasService: ArmasService, private empresaModalidadService: EmpresaModalidadesService) { }

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
      calibre: ['', [Validators.required, Validators.maxLength(10)]],
      bunker: ['', Validators.required],
      status: ['', Validators.required],
      personal: [''],
      serie: ['', [Validators.required, Validators.maxLength(30)]],
      matricula: ['', [Validators.required, Validators.maxLength(30)]]
    })

    this.empresaLicenciaService.obtenerLicenciasColectivas().subscribe((data: EmpresaLicenciaColectiva[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las licencias colectivas. Motivo: ${error}`,
        ToastType.ERROR
      )
    });

    this.empresaPersonalService.obtenerPersonal().subscribe((data: Persona[]) => {
      this.personal = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `no se han podido descargar las licencias colectivas. Motivo: ${error}`,
        ToastType.ERROR
      )
    });

    this.domiciliosService.obtenerDomicilios().subscribe((data: EmpresaDomicilio[]) => {
      this.domicilios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.armasService.obtenerArmaMarcas().subscribe((data: ArmaMarca[]) => {
      this.marcas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las maracas de las armas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.armasService.obtenerArmaClases().subscribe((data: ArmaClase[]) => {
      this.clases = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las maracas de las armas. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaModalidadService.obtenerModalidades().subscribe((data: EmpresaModalidad[]) => {
      this.modalidades = data;
      console.log(this.modalidades);
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

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el domicilio en la licencia colectiva",
      ToastType.INFO
    );

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

    this.empresaLicenciaService.guardarDomicilioEnLicenciaColectiva(this.licencia.uuid, data).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el domicilio con exito",
        ToastType.SUCCESS
      );
      this.mostrarFormularioNuevaDireccion();
      this.empresaLicenciaService.obtenerDomiciliosPorLicenciaColectiva(this.licencia.uuid).subscribe((data: EmpresaDomicilio[]) => {
        this.domiciliosLicenciaColectiva = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los domicilios. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
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
    if(existeModalidad !== undefined && existeModalidad?.uuid !== this.licencia.uuid) {
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

    this.empresaLicenciaService.modificarLicenciaColectiva(this.licencia.uuid, formData).subscribe((data) => {
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

    this.empresaLicenciaService.obtenerLicenciaColectivaPorUuid(licenciaUuid).subscribe((data: EmpresaLicenciaColectiva) => {
      this.licencia = data;

      this.empresaLicenciaService.obtenerDomiciliosPorLicenciaColectiva(this.licencia.uuid).subscribe((data: EmpresaDomicilio[]) => {
        this.domiciliosLicenciaColectiva = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los domicilios de la licencia colectiva. Motivo: {error}`,
          ToastType.ERROR
        )
      })

      this.empresaLicenciaService.obtenerArmasPorLicenciaColectivaUuid(this.licencia.uuid).subscribe((data: Arma[]) => {
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

  custodiaArma() {
    if(this.model.editorData === '') {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Favor de agregar mas informacion en el relato de hechos`,
        ToastType.INFO
      );
      return;
    }

    let formData = new FormData();
    formData.append('relatoHechos', JSON.stringify(this.model.editorData));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaLicenciaService.custodiaArma(this.licencia.uuid, this.arma.uuid, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha cambiado el status del arma con exito",
        ToastType.SUCCESS
      );
      this.mostrarCambioStatusForm(undefined);
      this.empresaLicenciaService.obtenerArmasPorLicenciaColectivaUuid(this.licencia.uuid).subscribe((data: Arma[]) => {
        this.armas = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las armas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido cambiar el status del arma. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarCambioStatusForm(uuid) {
    this.mostrarModificarStatusArma = !this.mostrarModificarStatusArma;
    if(uuid !== undefined) {
      this.arma = this.armas.filter(x => uuid === x.uuid)[0];
    }
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
    licencia.submodalidad = this.modalidades.filter(x => x.modalidad.uuid === formValue.modalidad)[0].submodalidad;
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

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la licencia",
      ToastType.INFO
    );

    let formData = new FormData();
    formData.append('archivo', this.tempFile, this.tempFile.name);
    formData.append('licencia', JSON.stringify(licencia));

    this.empresaLicenciaService.guardarLicenciaColectiva(formData).subscribe((data: EmpresaLicenciaColectiva) => {
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

    this.empresaLicenciaService.descargarLicenciaPdf(this.licencia.uuid).subscribe((data: Blob) => {
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
      bunker: this.arma.bunker.uuid,
      serie: this.arma.serie,
      matricula: this.arma.matricula,
      persona: this.arma.personal?.uuid,
      status: this.arma.status
    });

    this.status = this.arma.status;
  }

  mostrarModificarLicenciaModal() {
    this.crearEmpresaLicenciaForm.patchValue({
      numeroOficio: this.licencia.numeroOficio,
      modalidad: this.licencia.modalidad.uuid,
      submodalidad: this.licencia?.submodalidad?.uuid,
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
    formData.personal = this.personal.filter(x => x.uuid === form.value.personal)[0];

    if(this.editandoArma) {
      this.empresaLicenciaService.modificarArma(this.licencia.uuid, this.arma.uuid, formData).subscribe((data) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el arma con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioArma();
        this.empresaLicenciaService.obtenerArmasPorLicenciaColectivaUuid(this.licencia.uuid).subscribe((data: Arma[]) => {
          this.armas = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido obtener las armas. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el arma. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
    } else {
      this.empresaLicenciaService.guardarArma(this.licencia.uuid, formData).subscribe((data: Arma) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado el arma con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioArma();
        this.empresaLicenciaService.obtenerArmasPorLicenciaColectivaUuid(this.licencia.uuid).subscribe((data: Arma[]) => {
          this.armas = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido obtener las armas. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
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

    this.empresaLicenciaService.eliminarLicenciaColectiva(this.licencia.uuid).subscribe((data: EmpresaLicenciaColectiva) => {
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

    this.empresaLicenciaService.eliminarArma(this.licencia.uuid, this.tempUuidArma).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el arma de la licencia colectiva con exito",
        ToastType.SUCCESS
      );
      this.empresaLicenciaService.obtenerArmasPorLicenciaColectivaUuid(this.licencia.uuid).subscribe((data: Arma[]) => {
        this.armas = data;
        this.modal.close();
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido obtener las armas. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
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

    this.empresaLicenciaService.eliminarDomicilioEnLicenciaColectiva(this.licencia.uuid, this.tempUuidDireccion).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el domicilio de la licencia colectiva con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaLicenciaService.obtenerDomiciliosPorLicenciaColectiva(this.licencia.uuid).subscribe((data: EmpresaDomicilio[]) => {
        this.domiciliosLicenciaColectiva = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se pudieron descargar los domicilios. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
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
