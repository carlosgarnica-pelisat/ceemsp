import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../_services/toast.service";
import Stepper from "bs-stepper";
import EmpresaDomicilio from "../../_models/EmpresaDomicilio";
import Cliente from "../../_models/Cliente";
import {ToastType} from "../../_enums/ToastType";
import CanRaza from "../../_models/CanRaza";
import {CanesService} from "../../_services/canes.service";
import Can from "../../_models/Can";
import TipoEntrenamiento from "../../_models/TipoEntrenamiento";
import CanAdiestramiento from "../../_models/CanAdiestramiento";
import {faCheck, faTrash, faPencilAlt, faDownload} from "@fortawesome/free-solid-svg-icons";
import CanCartillaVacunacion from "../../_models/CanCartillaVacunacion";
import CanConstanciaSalud from "../../_models/CanConstanciaSalud";
import {DomiciliosService} from "../../_services/domicilios.service";
import {EmpresaCanesService} from "../../_services/empresa-canes.service";
import CanFotografia from "../../_models/CanFotografia";
import {formatDate} from "@angular/common";
import {BotonEmpresaCanesComponent} from "../../_components/botones/boton-empresa-canes/boton-empresa-canes.component";
import {ReporteoService} from "../../_services/reporteo.service";

@Component({
  selector: 'app-empresa-canes',
  templateUrl: './empresa-canes.component.html',
  styleUrls: ['./empresa-canes.component.css']
})
export class EmpresaCanesComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  faCheck = faCheck;
  faTrash = faTrash;
  faPencilAlt = faPencilAlt;
  faDownload = faDownload;

  domicilios: EmpresaDomicilio[] = [];
  clientes: Cliente[] = [];
  razas: CanRaza[] = [];
  tiposAdiestramiento: TipoEntrenamiento[] = [];

  columnDefs = [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true, hide: true, resizable: true },
    {
      headerName: "Cons.",
      valueGetter: "node.rowIndex + 1",
      pinned: "left",
      width: 70
    },
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true, pinned: 'left', resizable: true },
    {headerName: 'Raza', field: 'raza.nombre', sortable: true, filter: true, resizable: true},
    {headerName: 'Genero', field: 'genero', sortable: true, width: 150, resizable: true
      , filter: true},
    {headerName: 'Fecha de ingreso', field: 'fechaIngreso', width: 150, sortable: true, filter: true, resizable: true},
    {headerName: 'Asignacion', sortable: true, filter: true, resizable: true, width: 300, valueGetter: function(params) {
        if(params.data.status === 'ACTIVO') {
          return params.data.elementoAsignado?.nombres + " " + params.data.elementoAsignado?.apellidoPaterno + " " + params.data.elementoAsignado?.apellidoMaterno
        }
        return "Sin asignar"
      }},
    {headerName: 'Status de captura', sortable: true, resizable: true, filter: true, valueGetter: function(params) {
        if(params.data.fotografiaCapturada && params.data.adiestramientoCapturado && (params.data.vacunacionCapturada || params.data.constanciaCapturada)) {
          return 'COMPLETA'
        } else {
          return 'INCOMPLETA'
        }
      }},
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        editar: this.editar.bind(this),
        eliminar: this.eliminar.bind(this)
      }}
  ];
  allColumnDefs = Can.obtenerTodasLasColumnas();
  rowData = [];

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearEmpresaCanForm: FormGroup;
  crearEmpresaCanCertificadoSaludForm: FormGroup;
  crearEmpresaCanCartillaVacunacionForm: FormGroup;
  crearEmpresaCanEntrenamientoForm: FormGroup;
  crearCanFotografiaForm: FormGroup;
  motivosEliminacionForm: FormGroup;

  origen: string = "";

  stepper: Stepper;

  imagenPrincipal: any;

  pestanaActual: string = "DETALLES";
  can: Can;
  certificadoSalud: CanConstanciaSalud;
  constanciaSalud: CanConstanciaSalud;
  entrenamiento: CanAdiestramiento;

  tempFile;
  imagenActual;
  pdfActual;

  showEntrenamientoForm: boolean;
  showCertificadoForm: boolean;
  showVacunacionForm: boolean;
  showFotografiaForm: boolean;

  canGuardado: boolean = false;
  certificadoGuardado: boolean = false;
  vacunacionGuardada: boolean = false;
  entrenamientoGuardado: boolean = false;
  fotografiaGuardada: boolean = false;

  tipoAdiestramiento: TipoEntrenamiento;
  @ViewChild('verDetallesCanModal') verDetallesCanModal: any;
  @ViewChild('mostrarFotoCanesModal') mostrarFotoCanModal: any;
  @ViewChild('modificarCanModal') modificarCanModal: any;
  @ViewChild('eliminarCanModal') eliminarCanModal: any;
  @ViewChild('eliminarCanCartillaVacunacionModal') eliminarCanCartillaVacunacionModal: any;
  @ViewChild('eliminarCanCertificadoSaludModal') eliminarCanCertificadoSaludModal: any;
  @ViewChild('eliminarCanEntrenamientoModal') eliminarCanEntrenamientoModal: any;
  @ViewChild('eliminarCanFotografiaModal') eliminarCanFotografiaModal: any;
  @ViewChild('crearCartillaVacunacionModal') crearCartillaVacunacionModal: any;
  @ViewChild('crearCertificadoSaludModal') crearCertificadoSaludModal: any;
  @ViewChild('crearEntrenamientoModal') crearEntrenamientoModal: any;
  @ViewChild('crearFotografiaModal') crearFotografiaModal: any;

  tempUuidCartillaVacunacion: string = "";
  tempUuidCertificadoSalud: string = "";
  tempUuidEntrenamiento: string = "";
  tempUuidFotografia: string = "";
  status: string = "";

  editandoModal: boolean = false;

  fechaDeHoy = new Date().toISOString().split('T')[0];

  mostrarOtraRaza: boolean = false;

  canCartillaVacunacion: CanCartillaVacunacion;
  cancertificadoSalud: CanConstanciaSalud;
  canEntrenamiento: CanAdiestramiento;

  editandoCartillaVacunacion: boolean = false;
  editandoCertificadoSalud: boolean = false;
  editandoEntrenamiento: boolean = false;
  pdfBlob;

  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute,
              private toastService: ToastService, private modalService: NgbModal,
              private empresaCanesService: EmpresaCanesService, private canesService: CanesService,
              private empresaDomicilioService: DomiciliosService, private reporteoService: ReporteoService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaCanesComponent
    }

    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearEmpresaCanForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(50)]],
      genero: ['', Validators.required],
      raza: ['', Validators.required],
      razaOtro: ['', Validators.maxLength(50)],
      domicilioAsignado: ['', Validators.required],
      fechaIngreso: ['', Validators.required],
      edad: ['', [Validators.required, Validators.max(99), Validators.min(0)]],
      descripcion: ['', Validators.required],
      chip: ['', Validators.required],
      tatuaje: ['', Validators.required],
      origen: ['', Validators.required],
      status: [''],
      razonSocial: ['', Validators.maxLength(100)],
      fechaInicio: [''],
      fechaFin: [''],
      peso: ['', [Validators.required, Validators.max(99), Validators.min(0)]]
    });

    this.crearEmpresaCanCertificadoSaludForm = this.formBuilder.group({
      expedidoPor: ['', [Validators.required, Validators.maxLength(100)]],
      cedula: ['', [Validators.required, Validators.maxLength(20)]],
      fechaExpedicion: ['', Validators.required],
      archivo: ['', [Validators.required]]
    })

    this.crearEmpresaCanCartillaVacunacionForm = this.formBuilder.group({
      expedidoPor: ['', [Validators.required, Validators.maxLength(100)]],
      cedula: ['', [Validators.required, Validators.maxLength(20)]],
      fechaExpedicion: ['', Validators.required],
      archivo: ['', Validators.required]
    })

    this.crearEmpresaCanEntrenamientoForm = this.formBuilder.group({
      nombreInstructor: ['', [Validators.required, Validators.maxLength(100)]],
      tipoAdiestramiento: ['', Validators.required],
      fechaConstancia: ['', Validators.required],
      archivo: ['']
    })

    this.crearCanFotografiaForm = this.formBuilder.group({
      file: ['', Validators.required],
      descripcion: ['']
    })

    this.motivosEliminacionForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: ['', Validators.required],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });


    this.canesService.getAllEntrenamientos().subscribe((data: TipoEntrenamiento[]) => {
      this.tiposAdiestramiento = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los entrenamientos. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaCanesService.obtenerCanes().subscribe((data: Can[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se descargaron los canes. Motivo: ${error}`,
        ToastType.ERROR
      )
    });

    this.canesService.getAllRazas().subscribe((data: CanRaza[]) => {
      this.razas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las razas. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.empresaDomicilioService.obtenerDomicilios().subscribe((data: EmpresaDomicilio[]) => {
      this.domicilios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los domicilios de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      )
    });

    this.route.queryParams.subscribe((qp) => {
      if(qp.uuid !== undefined) {
        this.mostrarModalDetalles(qp)
      }
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Alguno de los parametros no es valido`,
        ToastType.ERROR
      );
    })

  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData)
  }

  editar(rowData) {
    if(rowData.rowData?.eliminado) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El elemento ya esta eliminado. No se puede editar`,
        ToastType.WARNING
      );
      return;
    }

    this.empresaCanesService.obtenerCanPorUuid(rowData?.rowData?.uuid).subscribe((data: Can) => {
      this.can = data;
      this.editandoModal = false;
      this.crearEmpresaCanForm.patchValue({
        nombre: this.can.nombre,
        genero: this.can.genero,
        raza: this.can.raza.uuid,
        razaOtro: '',
        domicilioAsignado: this.can.domicilioAsignado.uuid,
        fechaIngreso: this.can.fechaIngreso,
        edad: this.can.edad,
        descripcion: this.can.descripcion,
        chip: this.can.chip,
        tatuaje: this.can.tatuaje,
        origen: this.can.origen,
        status: this.can.status,
        razonSocial: this.can.razonSocial,
        fechaInicio: this.can.fechaInicio,
        fechaFin: this.can.fechaFin,
        peso: this.can.peso,
        elementoAsignado: this.can.elementoAsignado?.uuid
      });
      this.origen = this.can.origen;
      this.status = this.can.status;

      this.modal = this.modalService.open(this.modificarCanModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl', backdrop: 'static', keyboard: false});

      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el can. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  eliminar(rowData) {
    if(rowData.rowData?.eliminado) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El elemento ya esta eliminado. No se puede editar`,
        ToastType.WARNING
      );
      return;
    }

    this.empresaCanesService.obtenerCanPorUuid(rowData?.rowData?.uuid).subscribe((data: Can) => {
      this.can = data;
      this.mostrarModalEliminarCan();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el can. Motivo: ${error}`,
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

  seleccionarOrigen(event) {
    this.origen = event.value;
  }

  cambiarPestana(pestana) {
    this.pestanaActual = pestana;
  }

  mostrarModalDetalles(rowData) {
    let canUuid = rowData.uuid;

    this.empresaCanesService.obtenerCanPorUuid(canUuid).subscribe((data: Can) => {
      this.can = data;
      this.modal = this.modalService.open(this.verDetallesCanModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

      if(this.can?.fotografias.length > 0) {
        let canFoto = this.can?.fotografias[0];
        this.empresaCanesService.descargarCanFotografia(this?.can?.uuid, canFoto.uuid).subscribe((data: Blob) => {
          this.convertirImagenPrincipal(data);
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar la fotografia. Motivo: ${error}`,
            ToastType.ERROR
          )
        })
      } else {
        this.imagenPrincipal = undefined;
      }

      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion del can. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  convertirImagenPrincipal(imagen: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.imagenPrincipal = reader.result
    });

    if(imagen) {
      reader.readAsDataURL(imagen)
    }
  }

  esOtraRaza(event) {
    let otraRaza = this.razas.filter(x => x.uuid === event.value);
    if(otraRaza.length > 0 && otraRaza[0].nombre === "Otro") {
      this.mostrarOtraRaza = true;
    } else {
      this.mostrarOtraRaza = false;
    }

  }

  guardarCambiosCan(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos sin rellenar",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando los cambios en el can",
      ToastType.INFO
    );

    let can: Can = form.value;
    can.raza = this.razas.filter(x => x.uuid === form.value.raza)[0];
    can.domicilioAsignado = this.domicilios.filter(x => x.uuid === form.value.domicilioAsignado)[0];

    this.empresaCanesService.modificarCan(this.can.uuid, can).subscribe((data: Can) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado el can con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar el can. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
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

  mostrarEntrenamientoForm() {
    this.showEntrenamientoForm = !this.showEntrenamientoForm;
    if(!this.showEntrenamientoForm) {
      this.crearEmpresaCanEntrenamientoForm.reset();
    }
    if(this.editandoEntrenamiento) {
      this.editandoEntrenamiento = false;
      this.canEntrenamiento = undefined;
    }
  }

  mostrarCertificadoSaludForm() {
    this.showCertificadoForm = !this.showCertificadoForm;
    if(!this.showCertificadoForm) {
      this.crearEmpresaCanCertificadoSaludForm.reset();
    }
    if(this.editandoCertificadoSalud) {
      this.editandoCertificadoSalud = false;
      this.cancertificadoSalud = undefined;
    }
  }

  mostrarVacunacionForm() {
    this.showVacunacionForm = !this.showVacunacionForm;
    if(!this.showVacunacionForm) {
      this.crearEmpresaCanCartillaVacunacionForm.reset();
    }
    if(this.editandoCartillaVacunacion) {
      this.editandoCartillaVacunacion = false;
      this.canCartillaVacunacion = undefined;
    }
  }

  mostrarFormularioNuevaFotografia() {
    this.showFotografiaForm = !this.showFotografiaForm;
  }

  cambiarTipoAdiestramiento(event) {
    let uuid = event.value;
    this.tipoAdiestramiento = this.tiposAdiestramiento.filter(x => x.uuid === uuid)[0];
  }

  descargarConstanciaSalud(uuid, modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.empresaCanesService.descargarCanConstanciaSalud(this.can.uuid, uuid).subscribe((data: Blob) => {
      this.convertirPdf(data);
      this.pdfBlob = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la constancia de salud. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarCertificadoVacunacion(uuid, modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.empresaCanesService.descargarCanCartillaVacunacionPdf(this.can.uuid, uuid).subscribe((data: Blob) => {
      this.convertirPdf(data);
      this.pdfBlob = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el PDF. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarModalAgregarCartillaVacunacion() {
    this.modal = this.modalService.open(this.crearCartillaVacunacionModal, {size: 'xl', backdrop: 'static'})
  }

  mostrarModalAgregarCertificadoSalud() {
    this.modal = this.modalService.open(this.crearCertificadoSaludModal, {size: 'xl', backdrop: 'static'});
  }

  mostrarModalAgregarEntrenamiento() {
    this.modal = this.modalService.open(this.crearEntrenamientoModal, {size: 'xl', backdrop: 'static'});
  }

  mostrarModalAgregarFotografia() {
    this.modal = this.modalService.open(this.crearFotografiaModal, {size: 'xl', backdrop: 'static'})
  }

  mostrarModalEliminarCartillaVacunacion(uuid) {
    this.tempUuidCartillaVacunacion = uuid;

    this.modal = this.modalService.open(this.eliminarCanCartillaVacunacionModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarCertificadoSalud(uuid) {
    this.tempUuidCertificadoSalud = uuid;

    this.modal = this.modalService.open(this.eliminarCanCertificadoSaludModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarAdiestramiento(uuid) {
    this.tempUuidEntrenamiento = uuid;

    this.modal = this.modalService.open(this.eliminarCanEntrenamientoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarFotografia(uuid) {
    this.tempUuidFotografia = uuid;

    this.modal = this.modalService.open(this.eliminarCanFotografiaModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
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

  guardarAdiestramiento(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido rellenados",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el entrenamiento",
      ToastType.INFO
    );

    let formData: CanAdiestramiento = form.value;
    formData.canTipoAdiestramiento = this.tipoAdiestramiento;

    let constanciaSaludFormdata = new FormData();
    if(this.tempFile !== undefined) {
      constanciaSaludFormdata.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      constanciaSaludFormdata.append('archivo', null)
    }
    constanciaSaludFormdata.append('adiestramiento', JSON.stringify(formData));

    if(this.editandoEntrenamiento) {
      this.empresaCanesService.modificarCanEntrenamiento(this.can.uuid, this.canEntrenamiento.uuid, constanciaSaludFormdata).subscribe((data: CanAdiestramiento) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se ha guardado el can con exito`,
          ToastType.SUCCESS
        );
        this.modal.close();
        this.mostrarEntrenamientoForm();
        this.empresaCanesService.descargarCanAdiestramientos(this.can.uuid).subscribe((data: CanAdiestramiento[]) => {
          this.can.adiestramientos = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los adiestramientos. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido actualizar el adiestramiento del can. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.empresaCanesService.guardarCanAdiestramiento(this.can.uuid, constanciaSaludFormdata).subscribe((data) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado el adiestramiento en el can con exito",
          ToastType.SUCCESS
        );
        this.mostrarEntrenamientoForm();
        this.modal.close();
        this.empresaCanesService.descargarCanAdiestramientos(this.can.uuid).subscribe((data: CanAdiestramiento[]) => {
          this.can.adiestramientos = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los adiestramientos. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se pudo guardar el adiestramiento del can. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }
  }

  guardarCertificado(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos sin rellenar",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando la constancia de salud",
      ToastType.INFO
    );

    let value: CanConstanciaSalud = form.value;
    let constanciaSaludFormdata = new FormData();
    if(this.tempFile !== undefined) {
      constanciaSaludFormdata.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      constanciaSaludFormdata.append('archivo', null)
    }
    constanciaSaludFormdata.append('constanciaSalud', JSON.stringify(value));

    if(this.editandoCertificadoSalud) {
      this.empresaCanesService.modificarCanConstanciaSalud(this.can.uuid, this.cancertificadoSalud?.uuid, constanciaSaludFormdata).subscribe((data: CanConstanciaSalud) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado la constancia de salud en la base de datos",
          ToastType.SUCCESS
        );
        this.mostrarCertificadoSaludForm();
        this.modal.close();
        this.empresaCanesService.descargarCanConstanciasSalud(this.can.uuid).subscribe((data: CanConstanciaSalud[]) => {
          this.can.constanciasSalud = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los adiestramientos de nuevo. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el certificado de salud. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.empresaCanesService.guardarCanConstanciaSalud(this.can.uuid, constanciaSaludFormdata).subscribe((data: CanConstanciaSalud) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado la constancia de salud",
          ToastType.SUCCESS
        );
        this.mostrarCertificadoSaludForm();
        this.modal.close();
        this.empresaCanesService.descargarCanConstanciasSalud(this.can.uuid).subscribe((data: CanConstanciaSalud[]) => {
          this.can.constanciasSalud = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los adiestramientos de nuevo. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar la constancia de salud. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }
  }

  guardarCartillaVacunacuion(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos sin rellenar",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando la nueva cartilla de vacunacion",
      ToastType.INFO
    );

    let value: CanCartillaVacunacion = form.value;

    let formData = new FormData();
    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    formData.append('cartillaVacunacion', JSON.stringify(value));

    if(this.editandoCartillaVacunacion) {
      this.empresaCanesService.modificarCanCartillaVacunacion(this.can.uuid, this.canCartillaVacunacion.uuid, formData).subscribe((data: CanCartillaVacunacion) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se ha modificado la cartilla de vacunacion con exito`,
          ToastType.SUCCESS
        );
        this.mostrarVacunacionForm();
        this.modal.close();
        this.empresaCanesService.obtenerCanCartillasVacunacion(this.can.uuid).subscribe((data: CanCartillaVacunacion[]) => {
          this.can.cartillasVacunacion = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar las cartillas de vacunacion`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar la cartilla de vacunacion. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.empresaCanesService.guardarCanCartillaVacunacion(this.can.uuid, formData).subscribe((data: CanCartillaVacunacion) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado la cartilla de vacunacion con exito",
          ToastType.SUCCESS
        );
        this.mostrarVacunacionForm();
        this.modal.close();
        this.empresaCanesService.obtenerCanCartillasVacunacion(this.can.uuid).subscribe((data: CanCartillaVacunacion[]) => {
          this.can.cartillasVacunacion = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar las cartillas de vacunacion`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar la nueva cartilla de vacunacion. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
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

      //this.columnDefs.push(newColumnDef);
      this.gridApi.setColumnDefs(this.columnDefs);
    } else {
      this.columnDefs = this.columnDefs.filter(s => s.field !== field);
    }
  }

  descargarArchivoAdiestramiento(uuid, modal) {
    this.modal = this.modalService.open(modal, {size: "lg", keyboard: false, backdrop: "static"})

    this.empresaCanesService.descargarCanAdiestramiento(this.can.uuid, uuid).subscribe((data: Blob) => {
      this.convertirPdf(data);
      this.pdfBlob = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el entrenamiento. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  next(stepName: string, form) {
    switch (stepName) {
      case "CERTIFICADOS":
        if(this.canGuardado) {
          this.stepper.next();
        } else {
          if(!form.valid) {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              "El formulario no es valido. Favor de corregir los errores",
              ToastType.WARNING
            );
            return;
          }


        }

        let formValue: Can = form.value;

        // Validando fechas
        if(formValue.origen !== 'PROPIO') {
          if(formValue.fechaInicio === "" || formValue.fechaFin === "") {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              "Al ser un elemento en arrendamiento o comodato, requiere fecha de inicio o fin",
              ToastType.WARNING
            );
            return;
          }

          let fechaInicio = new Date(formValue.fechaInicio);
          let fechaFin = new Date(formValue.fechaFin);
          if(fechaInicio > fechaFin) {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              "La fecha de inicio es mayor que la del final",
              ToastType.WARNING
            )
            return;
          }
        }

        this.toastService.showGenericToast(
          "Espere un momento",
          "Estamos guardando el can",
          ToastType.INFO
        );

        formValue.raza = this.razas.filter(x => x.uuid === form.value.raza)[0];
        formValue.domicilioAsignado = this.domicilios.filter(x => x.uuid === form.value.domicilioAsignado)[0];
        formValue.clienteDomicilio = null;
        formValue.clienteAsignado = null;
        formValue.fechaInicio = form.controls["fechaInicio"].value.toLocaleString();
        formValue.fechaFin = form.controls["fechaFin"].value.toLocaleString();
        formValue.fechaIngreso = form.controls["fechaIngreso"].value.toLocaleString();
        formValue.status = "INSTALACIONES";
        formValue.elementoAsignado = null;

        this.empresaCanesService.guardarCan(formValue).subscribe((data: Can) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se ha guardado el can con exito",
            ToastType.SUCCESS
          );
          this.can = data;
          this.canGuardado = true;
          this.desactivarCamposEmpresa();
          this.stepper.next();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido guardar el cliente. ${error}`,
            ToastType.ERROR
          )
        });
        break;

      case "VACUNACION":
        if(this.certificadoGuardado) {
          this.stepper.next();
        } else {
          if(!form.valid) {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              "El formulario no es valido. Favor de corregir los errores",
              ToastType.WARNING
            );
            return;
          }

          this.toastService.showGenericToast(
            "Espera un momento",
            "Estamos guardando la nueva constancia de salud",
            ToastType.INFO
          );

          let constanciaSalud: CanConstanciaSalud = form.value;

          let constanciaSaludFormdata = new FormData();
          constanciaSaludFormdata.append('archivo', this.tempFile, this.tempFile.name);
          constanciaSaludFormdata.append('constanciaSalud', JSON.stringify(constanciaSalud));

          this.empresaCanesService.guardarCanConstanciaSalud(this.can.uuid, constanciaSaludFormdata).subscribe((data: CanConstanciaSalud) => {
            this.toastService.showGenericToast(
              "Listo",
              "Se ha guardado la constancia de salud",
              ToastType.SUCCESS
            );
            this.certificadoGuardado = true;
            this.certificadoSalud = constanciaSalud;
            this.desactivarCamposCertificadoSalud();
            this.stepper.next();
          }, (error) => {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              `No se ha podido guardar la constancia de salud. Motivo: ${error}`,
              ToastType.ERROR
            )
          })
        }
        break;
      case "ENTRENAMIENTO":
        if(this.vacunacionGuardada) {
          this.stepper.next()
        } else {
          if(!form.valid) {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              "El formulario no es valido. Favor de corregir los errores",
              ToastType.WARNING
            );
            return;
          }

          this.toastService.showGenericToast(
            "Espera un momento",
            "Estamos guardando la nueva cartilla de vacunacion",
            ToastType.INFO
          );

          let value: CanCartillaVacunacion = form.value;

          let formDataVacunacion = new FormData();
          formDataVacunacion.append('archivo', this.tempFile, this.tempFile.name);
          formDataVacunacion.append('cartillaVacunacion', JSON.stringify(value));

          this.empresaCanesService.guardarCanCartillaVacunacion(this.can.uuid, formDataVacunacion).subscribe((data: CanCartillaVacunacion) => {
            this.toastService.showGenericToast(
              "Listo",
              "Se ha guardado la cartilla de vacunacion con exito",
              ToastType.SUCCESS
            );
            this.desactivarCamposVacunacion();
            this.canCartillaVacunacion = value;
            this.vacunacionGuardada = true;
            this.stepper.next();
          }, (error) => {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              `No se ha podido guardar la nueva cartilla de vacunacion`,
              ToastType.ERROR
            );
          })
        }
        break;
      case "FOTOGRAFIA":
        if(this.entrenamientoGuardado) {
          this.stepper.next();
        } else {
          if(!form.valid) {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              "El formulario no es valido. Favor de corregir los errores",
              ToastType.WARNING
            );
            return;
          }

          this.toastService.showGenericToast(
            "Espere un momento",
            "Estamos guardando el entrenamiento",
            ToastType.INFO
          );

          let formData: CanAdiestramiento = form.value;
          formData.canTipoAdiestramiento = this.tipoAdiestramiento;

          let constanciaSaludFormdataCrear = new FormData();
          if(this.tempFile !== undefined) {
            constanciaSaludFormdataCrear.append('archivo', this.tempFile, this.tempFile.name);
          } else {
            constanciaSaludFormdataCrear.append('archivo', null)
          }
          constanciaSaludFormdataCrear.append('adiestramiento', JSON.stringify(formData));

          this.empresaCanesService.guardarCanAdiestramiento(this.can.uuid, constanciaSaludFormdataCrear).subscribe((data: CanAdiestramiento) => {
            this.toastService.showGenericToast(
              "Listo",
              "Se ha guardado el adiestramiento en el can con exito",
              ToastType.SUCCESS
            );
            this.entrenamientoGuardado = true;
            this.entrenamiento = formData;
            this.desactivarCamposEntrenamiento();
            this.stepper.next();
          }, (error) => {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              `No se pudo guardar el adiestramiento del can. Motivo: ${error}`,
              ToastType.ERROR
            )
          })
        }
        break;
      case "RESUMEN":
        if(this.fotografiaGuardada) {
          this.stepper.next()
        } else {
          if(!form.valid) {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              "El formulario no es valido. Favor de corregir los errores",
              ToastType.WARNING
            );
            return;
          }

          this.toastService.showGenericToast(
            "Espere un momento",
            "Estamos guardando la fotografia del can",
            ToastType.INFO
          );

          let fotografiaForm = form.value;
          let fotografiaFormData = new FormData();
          fotografiaFormData.append('fotografia', this.tempFile, this.tempFile.name);
          fotografiaFormData.append('metadataArchivo', JSON.stringify(fotografiaForm));

          this.empresaCanesService.guardarCanFotografia(this.can.uuid, fotografiaFormData).subscribe((data) => {
            this.toastService.showGenericToast(
              "Listo",
              "Se ha guardado la fotografia con exito",
              ToastType.SUCCESS
            );
            this.fotografiaGuardada = true;
            this.stepper.next();
          }, (error) => {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              `No se ha podido guardar la fotografia. Motivo: ${error}`,
              ToastType.ERROR
            )
          })
        }
        break;
    }
  }

  previous() {
    this.stepper.previous()
  }

  omitirPaso(paso) {
    this.stepper.next()
  }

  actualizarPagina() {
    window.location.reload();
  }

  cancelarCambiosCan() {
    this.crearEmpresaCanForm.reset()
    this.modal.close();
    this.crearEmpresaCanForm.controls['nombre'].enable();
    this.crearEmpresaCanForm.controls['genero'].enable();
    this.crearEmpresaCanForm.controls['raza'].enable();
    this.crearEmpresaCanForm.controls['razaOtro'].enable();
    this.crearEmpresaCanForm.controls['domicilioAsignado'].enable();
    this.crearEmpresaCanForm.controls['fechaIngreso'].enable();
    this.crearEmpresaCanForm.controls['edad'].enable();
    this.crearEmpresaCanForm.controls['descripcion'].enable();
    this.crearEmpresaCanForm.controls['chip'].enable();
    this.crearEmpresaCanForm.controls['tatuaje'].enable();
    this.crearEmpresaCanForm.controls['origen'].enable();
    this.crearEmpresaCanForm.controls['status'].enable();
    this.crearEmpresaCanForm.controls['razonSocial'].enable();
    this.crearEmpresaCanForm.controls['fechaInicio'].enable();
    this.crearEmpresaCanForm.controls['fechaFin'].enable();
    this.crearEmpresaCanForm.controls['peso'].enable();
  }

  private desactivarCamposEmpresa() {
    this.crearEmpresaCanForm.controls['nombre'].disable();
    this.crearEmpresaCanForm.controls['genero'].disable();
    this.crearEmpresaCanForm.controls['raza'].disable();
    this.crearEmpresaCanForm.controls['razaOtro'].disable();
    this.crearEmpresaCanForm.controls['domicilioAsignado'].disable();
    this.crearEmpresaCanForm.controls['fechaIngreso'].disable();
    this.crearEmpresaCanForm.controls['edad'].disable();
    this.crearEmpresaCanForm.controls['descripcion'].disable();
    this.crearEmpresaCanForm.controls['chip'].disable();
    this.crearEmpresaCanForm.controls['tatuaje'].disable();
    this.crearEmpresaCanForm.controls['origen'].disable();
    this.crearEmpresaCanForm.controls['status'].disable();
    this.crearEmpresaCanForm.controls['razonSocial'].disable();
    this.crearEmpresaCanForm.controls['fechaInicio'].disable();
    this.crearEmpresaCanForm.controls['fechaFin'].disable();
    this.crearEmpresaCanForm.controls['peso'].disable();
  }

  private desactivarCamposCertificadoSalud() {
    this.crearEmpresaCanCertificadoSaludForm.controls['expedidoPor'].disable();
    this.crearEmpresaCanCertificadoSaludForm.controls['cedula'].disable();
    this.crearEmpresaCanCertificadoSaludForm.controls['fechaExpedicion'].disable();
    this.crearEmpresaCanCertificadoSaludForm.controls['archivo'].disable();
  }

  private desactivarCamposVacunacion() {
    this.crearEmpresaCanCartillaVacunacionForm.controls['expedidoPor'].disable();
    this.crearEmpresaCanCartillaVacunacionForm.controls['cedula'].disable();
    this.crearEmpresaCanCartillaVacunacionForm.controls['fechaExpedicion'].disable();
    this.crearEmpresaCanCartillaVacunacionForm.controls['archivo'].disable();
  }

  private desactivarCamposEntrenamiento() {
    this.crearEmpresaCanEntrenamientoForm.controls['nombreInstructor'].disable();
    this.crearEmpresaCanEntrenamientoForm.controls['tipoAdiestramiento'].disable();
    this.crearEmpresaCanEntrenamientoForm.controls['fechaConstancia'].disable();
  }

  mostrarModalModificarCan() {
    this.editandoModal = true;
    this.crearEmpresaCanForm.patchValue({
      nombre: this.can.nombre,
      genero: this.can.genero,
      raza: this.can.raza.uuid,
      razaOtro: '',
      domicilioAsignado: this.can.domicilioAsignado.uuid,
      fechaIngreso: this.can.fechaIngreso,
      edad: this.can.edad,
      descripcion: this.can.descripcion,
      chip: this.can.chip,
      tatuaje: this.can.tatuaje,
      origen: this.can.origen,
      status: this.can.status,
      razonSocial: this.can.razonSocial,
      fechaInicio: this.can.fechaInicio,
      fechaFin: this.can.fechaFin,
      peso: this.can.peso,
      elementoAsignado: this.can.elementoAsignado?.uuid
    });
    this.origen = this.can.origen;
    this.status = this.can.status;

    this.modal = this.modalService.open(this.modificarCanModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl', backdrop: 'static'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  mostrarModalEditarVacunacion(uuid) {
    this.canCartillaVacunacion = this.can.cartillasVacunacion.filter(x => x.uuid === uuid)[0];
    this.mostrarVacunacionForm();
    this.modal = this.modalService.open(this.crearCartillaVacunacionModal, {size: 'xl', backdrop: 'static'})
    this.editandoCartillaVacunacion = true;
    this.crearEmpresaCanCartillaVacunacionForm.patchValue({
      expedidoPor: this.canCartillaVacunacion.expedidoPor,
      cedula: this.canCartillaVacunacion.cedula,
      fechaExpedicion: this.canCartillaVacunacion.fechaExpedicion
    });
    this.crearEmpresaCanCartillaVacunacionForm.controls['archivo'].clearValidators();
    this.crearEmpresaCanCartillaVacunacionForm.controls['archivo'].updateValueAndValidity();
  }

  mostrarModalEditarCertificado(uuid) {
    this.cancertificadoSalud = this.can.constanciasSalud.filter(x => x.uuid === uuid)[0];
    this.mostrarCertificadoSaludForm();
    this.modal = this.modalService.open(this.crearCertificadoSaludModal, {size: 'xl', backdrop: 'static'})
    this.editandoCertificadoSalud = true;
    this.crearEmpresaCanCertificadoSaludForm.patchValue({
      expedidoPor: this.cancertificadoSalud.expedidoPor,
      cedula: this.cancertificadoSalud.cedula,
      fechaExpedicion: this.cancertificadoSalud.fechaExpedicion
    });
    this.crearEmpresaCanCertificadoSaludForm.controls['archivo'].clearValidators();
    this.crearEmpresaCanCertificadoSaludForm.controls['archivo'].updateValueAndValidity();
  }

  mostrarModalEditarAdiestramiento(uuid) {
    this.canEntrenamiento = this.can.adiestramientos.filter(x => x.uuid === uuid)[0];
    this.mostrarEntrenamientoForm();
    this.modal = this.modalService.open(this.crearEntrenamientoModal, {size: "xl", backdrop: "static"})
    this.editandoEntrenamiento = true;
    this.crearEmpresaCanEntrenamientoForm.patchValue({
      nombreInstructor: this.canEntrenamiento.nombreInstructor,
      tipoAdiestramiento: this.canEntrenamiento.canTipoAdiestramiento.uuid,
      fechaConstancia: this.canEntrenamiento.fechaConstancia
    });
    this.tipoAdiestramiento = this.canEntrenamiento.canTipoAdiestramiento;
  }

  mostrarModalEliminarCan() {
    this.motivosEliminacionForm.patchValue({
      fechaBaja: formatDate(new Date(), "yyyy-MM-dd", "en")
    });
    this.motivosEliminacionForm.controls['fechaBaja'].disable();

    this.modal = this.modalService.open(this.eliminarCanModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  confirmarEliminarCan(form) {

    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario es invalido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el can",
      ToastType.INFO
    );

    let formValue: Can = form.value;
    console.log(form.value);

    let formData = new FormData();
    formData.append('can', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaCanesService.eliminarCan(this.can.uuid, formData).subscribe(() => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el can con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El can no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarCartillaVacunacionCan() {
    if(this.tempUuidCartillaVacunacion === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID de la cartilla de vacunacion a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando la cartilla de vacunacion",
      ToastType.INFO
    );

    this.empresaCanesService.eliminarCanCartillaVacunacion(this.can.uuid, this.tempUuidCartillaVacunacion).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la cartilla de vacunacion con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaCanesService.obtenerCanCartillasVacunacion(this.can.uuid).subscribe((data: CanCartillaVacunacion[]) => {
        this.can.cartillasVacunacion = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las cartillas de vacunacion. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `La cartilla de vacunacion no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarCertificadoSaludCan() {
    if(this.tempUuidCertificadoSalud === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del certificado de salud a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el certificado de salud",
      ToastType.INFO
    );

    this.empresaCanesService.eliminarCanConstanciaSalud(this.can.uuid, this.tempUuidCertificadoSalud).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el certificado de salud con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaCanesService.descargarCanConstanciasSalud(this.can.uuid).subscribe((data: CanConstanciaSalud[]) => {
        this.can.constanciasSalud = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los adiestramientos de nuevo. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El certificado de salud no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarEntrenamientoCan() {
    if(this.tempUuidCertificadoSalud === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del entrenamiento a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el entrenamiento",
      ToastType.INFO
    );

    this.empresaCanesService.eliminarCanEntrenamiento(this.can.uuid, this.tempUuidEntrenamiento).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el entrenamiento con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaCanesService.descargarCanAdiestramientos(this.can.uuid).subscribe((data: CanAdiestramiento[]) => {
        this.can.adiestramientos = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los adiestramientos. Motivo: ${error}`,
          ToastType.ERROR
        )
      });
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El entrenamiento no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarFotografiaCan() {
    if(this.tempUuidFotografia === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID de la fotografia a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando la fotografia",
      ToastType.INFO
    );

    this.empresaCanesService.eliminarCanFotografia(this.can.uuid, this.tempUuidFotografia).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la fotografia con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaCanesService.listarCanFotografias(this.can.uuid).subscribe((data: CanFotografia[]) => {
        this.can.fotografias = data;
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
        `El entrenamiento no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarFotografia(uuid) {
    this.empresaCanesService.descargarCanFotografia(this.can.uuid, uuid).subscribe((data) => {
      // @ts-ignore
      this.convertirImagen(data);
      this.modalService.open(this.mostrarFotoCanModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la fotografia de la persona. Motivo: ${error}`,
        ToastType.ERROR
      )
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

  guardarFotografia(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay algunos campos pendientes",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la fotografia del can",
      ToastType.INFO
    );

    let formValue = form.value;
    let formData = new FormData();
    formData.append('fotografia', this.tempFile, this.tempFile.name);
    formData.append('metadataArchivo', JSON.stringify(formValue));

    this.empresaCanesService.guardarCanFotografia(this.can.uuid, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la fotografia con exito",
        ToastType.SUCCESS
      );
      this.mostrarFormularioNuevaFotografia();
      this.empresaCanesService.listarCanFotografias(this.can.uuid).subscribe((data: CanFotografia[]) => {
        this.can.fotografias = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido obtener las fotografias. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la fotografia. Motivo: ${error}`,
        ToastType.ERROR
      )
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

  isColumnListed(field: string ) {
    return this.columnDefs.filter(s => s.field === field)[0] !== undefined;
  }

  generarReporteExcel() {
    this.reporteoService.generarReporteCanes().subscribe((data) => {
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(data);
      link.download = "test.xls";
      link.click();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el reporte en excel. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  descargarDocumentoCartillaVacunacion() {
    let link = document.createElement('a');
    link.href = window.URL.createObjectURL(this.pdfBlob);
    link.download = "cartilla-vacunacion.pdf";
    link.click();
  }

  descargarDocumentoEntrenamiento() {
    let link = document.createElement('a');
    link.href = window.URL.createObjectURL(this.pdfBlob);
    link.download = "entrenamiento.pdf";
    link.click();
  }

  descargarDocumentoCertificadoSalud() {
    let link = document.createElement('a');
    link.href = window.URL.createObjectURL(this.pdfBlob);
    link.download = "certificado-salud.pdf";
    link.click();
  }

}
