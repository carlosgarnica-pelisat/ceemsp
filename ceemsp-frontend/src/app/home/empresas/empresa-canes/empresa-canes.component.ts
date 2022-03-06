import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../../_services/toast.service";
import Stepper from "bs-stepper";
import {EmpresaService} from "../../../_services/empresa.service";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import Cliente from "../../../_models/Cliente";
import {ToastType} from "../../../_enums/ToastType";
import CanRaza from "../../../_models/CanRaza";
import {CanesService} from "../../../_services/canes.service";
import Can from "../../../_models/Can";
import TipoEntrenamiento from "../../../_models/TipoEntrenamiento";
import CanAdiestramiento from "../../../_models/CanAdiestramiento";
import {faCheck, faDownload, faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import CanCartillaVacunacion from "../../../_models/CanCartillaVacunacion";
import CanConstanciaSalud from "../../../_models/CanConstanciaSalud";

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

  columnDefs = Can.obtenerColumnasPorDefault();
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

  origen: string = "";

  stepper: Stepper;

  pestanaActual: string = "DETALLES";
  can: Can;

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

  tipoAdiestramiento: TipoEntrenamiento;
  @ViewChild('mostrarFotoCanesModal') mostrarFotoCanModal: any;


  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute,
              private toastService: ToastService, private modalService: NgbModal,
              private empresaService: EmpresaService, private canesService: CanesService) { }

  ngOnInit(): void {
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
      elementoAsignado: [''],
      clienteAsignado: [''],
      domicilioClienteAsignado: [''],
      motivos: [''],
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
      fechaConstancia: ['', Validators.required]
    })

    this.crearCanFotografiaForm = this.formBuilder.group({
      file: ['', Validators.required],
      descripcion: ['', Validators.required]
    })

    this.canesService.getAllEntrenamientos().subscribe((data: TipoEntrenamiento[]) => {
      this.tiposAdiestramiento = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los entrenamientos. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaService.obtenerCanes(this.uuid).subscribe((data: Can[]) => {
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

    this.empresaService.obtenerDomicilios(this.uuid).subscribe((data: EmpresaDomicilio[]) => {
      this.domicilios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los domicilios de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      )
    });

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

  mostrarModalDetalles(rowData, modal) {
    let canUuid = rowData.uuid;

    this.empresaService.obtenerCanPorUuid(this.uuid, canUuid).subscribe((data: Can) => {
      this.can = data;
      this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

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
  }

  mostrarCertificadoSaludForm() {
    this.showCertificadoForm = !this.showCertificadoForm;
  }

  mostrarVacunacionForm() {
    this.showVacunacionForm = !this.showVacunacionForm;
  }

  mostrarFormularioNuevaFotografia() {
    this.showFotografiaForm = !this.showFotografiaForm;
  }

  cambiarTipoAdiestramiento(event) {
    let uuid = event.value;
    this.tipoAdiestramiento = this.tiposAdiestramiento.filter(x => x.uuid === uuid)[0];
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

    this.empresaService.guardarCanAdiestramiento(this.uuid, this.can.uuid, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el adiestramiento en el can con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo guardar el adiestramiento del can. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
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
      "Estamos guardando la nueva cartilla de vacunacion",
      ToastType.INFO
    );

    let value: CanConstanciaSalud = form.value;
    let constanciaSaludFormdata = new FormData();
    constanciaSaludFormdata.append('archivo', this.tempFile, this.tempFile.name);
    constanciaSaludFormdata.append('constanciaSalud', JSON.stringify(value));

    this.empresaService.guardarCanConstanciaSalud(this.uuid, this.can.uuid, constanciaSaludFormdata).subscribe((data: CanConstanciaSalud) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la constancia de salud",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la constancia de salud. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
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
    formData.append('archivo', this.tempFile, this.tempFile.name);
    formData.append('cartillaVacunacion', JSON.stringify(value));

    this.empresaService.guardarCanCartillaVacunacion(this.uuid, this.can.uuid, formData).subscribe((data: CanCartillaVacunacion) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la cartilla de vacunacion con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la nueva cartilla de vacunacion`,
        ToastType.ERROR
      );
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

  modify() {

  }

  delete() {

  }

  next(stepName: string, form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenarse",
        ToastType.WARNING
      );
      return;
    }

    switch (stepName) {
      case "CERTIFICADOS":

        this.toastService.showGenericToast(
          "Espere un momento",
          "Estamos guardando el can",
          ToastType.INFO
        );

        let formValue: Can = form.value;

        formValue.raza = this.razas.filter(x => x.uuid === form.value.raza)[0];
        formValue.domicilioAsignado = this.domicilios.filter(x => x.uuid === form.value.domicilioAsignado)[0];
        formValue.clienteDomicilio = null;
        formValue.clienteAsignado = null;
        formValue.status = "INSTALACIONES";
        formValue.elementoAsignado = null;

        this.empresaService.guardarCan(this.uuid, formValue).subscribe((data: Can) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se ha guardado el can con exito",
            ToastType.SUCCESS
          );
          this.can = data;
          this.canGuardado = true;
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
        this.toastService.showGenericToast(
          "Espera un momento",
          "Estamos guardando la nueva constancia de salud",
          ToastType.INFO
        );

        let constanciaSalud: CanConstanciaSalud = form.value;

        let constanciaSaludFormdata = new FormData();
        constanciaSaludFormdata.append('archivo', this.tempFile, this.tempFile.name);
        constanciaSaludFormdata.append('constanciaSalud', JSON.stringify(constanciaSalud));

        this.empresaService.guardarCanConstanciaSalud(this.uuid, this.can.uuid, constanciaSaludFormdata).subscribe((data: CanConstanciaSalud) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se ha guardado la constancia de salud",
            ToastType.SUCCESS
          );
          this.certificadoGuardado = true;
          this.stepper.next();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido guardar la constancia de salud. Motivo: ${error}`,
            ToastType.ERROR
          )
        })
        break;

      case "ENTRENAMIENTO":
        this.toastService.showGenericToast(
          "Espera un momento",
          "Estamos guardando la nueva cartilla de vacunacion",
          ToastType.INFO
        );

        let value: CanCartillaVacunacion = form.value;

        let formDataVacunacion = new FormData();
        formDataVacunacion.append('archivo', this.tempFile, this.tempFile.name);
        formDataVacunacion.append('cartillaVacunacion', JSON.stringify(value));

        this.empresaService.guardarCanCartillaVacunacion(this.uuid, this.can.uuid, formDataVacunacion).subscribe((data: CanCartillaVacunacion) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se ha guardado la cartilla de vacunacion con exito",
            ToastType.SUCCESS
          );
          this.vacunacionGuardada = true;
          this.stepper.next();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido guardar la nueva cartilla de vacunacion`,
            ToastType.ERROR
          );
        })
        break;

      case "RESUMEN":
        this.toastService.showGenericToast(
          "Espere un momento",
          "Estamos guardando el entrenamiento",
          ToastType.INFO
        );

        let formData: CanAdiestramiento = form.value;
        formData.canTipoAdiestramiento = this.tipoAdiestramiento;

        this.empresaService.guardarCanAdiestramiento(this.uuid, this.can.uuid, formData).subscribe((data) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se ha guardado el adiestramiento en el can con exito",
            ToastType.SUCCESS
          );
          this.entrenamientoGuardado = true;
          this.stepper.next();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se pudo guardar el adiestramiento del can. Motivo: ${error}`,
            ToastType.ERROR
          )
        })
        break;
    }
  }

  previous() {
    this.stepper.previous()
  }

  actualizarPagina() {
    window.location.reload();
  }

  descargarFotografia(uuid) {
    this.empresaService.descargarPersonaFotografia(this.uuid, this.can.uuid, uuid).subscribe((data) => {
      // @ts-ignore
      this.convertirImagen(data);
      this.modalService.open(this.mostrarFotoCanModal);
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la fotografia de la persona. Motivo: ${error}`,
        ToastType.ERROR
      )
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

    this.empresaService.guardarPersonaFotografia(this.uuid, this.can.uuid, formData).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la fotografia con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
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

}
