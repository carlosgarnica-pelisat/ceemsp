import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import VehiculoMarca from "../../../_models/VehiculoMarca";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import VehiculoSubmarca from "../../../_models/VehiculoSubmarca";
import {VehiculosService} from "../../../_services/vehiculos.service";
import {ToastType} from "../../../_enums/ToastType";
import VehiculoTipo from "../../../_models/VehiculoTipo";
import {ActivatedRoute} from "@angular/router";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import VehiculoUso from "../../../_models/VehiculoUso";
import Stepper from "bs-stepper";
import Vehiculo from "../../../_models/Vehiculo";
import VehiculoColor from "../../../_models/VehiculoColor";
import {faCheck, faDownload, faTrash} from "@fortawesome/free-solid-svg-icons";
import ExisteVehiculo from "../../../_models/ExisteVehiculo";
import {ValidacionService} from "../../../_services/validacion.service";

@Component({
  selector: 'app-empresa-vehiculos',
  templateUrl: './empresa-vehiculos.component.html',
  styleUrls: ['./empresa-vehiculos.component.css']
})
export class EmpresaVehiculosComponent implements OnInit {

  fechaDeHoy = new Date().toISOString().split('T')[0];

  private gridApi;
  private gridColumnApi;

  columnDefs = Vehiculo.obtenerColumnasPorDefault();
  allColumnDefs = Vehiculo.obtenerTodasLasColumnas();

  rowData = [];
  vehiculo: Vehiculo;

  showColorForm: boolean = false;
  showFotografiaForm: boolean = false;

  pestanaActual: string = "DETALLES";

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearVehiculoForm: FormGroup;
  crearColorForm: FormGroup;
  crearVehiculoFotografiaForm: FormGroup;

  marca: VehiculoMarca;
  marcas: VehiculoMarca[];
  submarcas: VehiculoSubmarca[];
  tipos: VehiculoTipo[];
  domicilios: EmpresaDomicilio[] = [];
  usos: VehiculoUso[] = [];

  blindado: boolean = false;
  origen: string = "";

  stepper: Stepper;

  faCheck = faCheck;
  faTrash = faTrash;
  faDownload = faDownload;

  imagenActual;
  tempFile;
  pdfActual;

  existeVehiculo: ExisteVehiculo;

  coloresTemp: VehiculoColor[] = [];

  @ViewChild('mostrarFotoVehiculoModal') mostrarFotoVehiculoModal: any;

  constructor(private modalService: NgbModal, private toastService: ToastService,
              private empresaService: EmpresaService, private formBuilder: FormBuilder,
              private vehiculosService: VehiculosService, private route: ActivatedRoute,
              private validacionService: ValidacionService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearVehiculoForm = this.formBuilder.group({
      placas: ['', [Validators.required, Validators.maxLength(15)]],
      serie: ['', [Validators.required, Validators.maxLength(30)]],
      tipo: ['', Validators.required],
      marca: ['', Validators.required],
      submarca: ['', Validators.required],
      anio: ['', Validators.required],
      rotulado: ['', Validators.required],
      uso: ['', Validators.required],
      domicilio: ['', Validators.required],
      origen: ['', Validators.required],
      blindado: ['', Validators.required],
      serieBlindaje: ['', Validators.maxLength(30)],
      fechaBlindaje: [''],
      numeroHolograma: ['', Validators.maxLength(30)],
      placaMetalica: ['', Validators.maxLength(30)],
      empresaBlindaje: ['', Validators.maxLength(50)],
      nivelBlindaje: [''],
      razonSocial: ['', Validators.maxLength(100)],
      fechaInicio: [''],
      fechaFin: ['']
      // TODO: Agregar campos para fotos y documentos; asi como constancia de blindaje
    })

    this.crearColorForm = this.formBuilder.group({
      color: ['', Validators.required],
      descripcion: ['', Validators.required]
    })

    this.crearVehiculoFotografiaForm = this.formBuilder.group({
      'file': ['', Validators.required],
      'descripcion': ['', Validators.required]
    });

    this.empresaService.obtenerVehiculos(this.uuid).subscribe((data: Vehiculo[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los vehiculos. ${error}`,
        ToastType.ERROR
      )
    })
  }

  modify() {

  }

  delete() {

  }

  cambiarPestana(pestana) {
    this.pestanaActual = pestana;
  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  seleccionarBlindado(event) {
    this.blindado = event.value === "true";
  }

  seleccionarOrigen(event) {
    this.origen = event.value;
    this.crearVehiculoForm.patchValue({
      fechaInicio: '',
      fechaFin: ''
    })
  }

  seleccionarMarca(event) {
    let marcaUuid = event.value;

    this.vehiculosService.obtenerVehiculoMarcaPorUuid(marcaUuid).subscribe((data: VehiculoMarca) => {
      this.marca = data;
      this.submarcas = data.submarcas;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la marca. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  mostrarFormularioColor() {
    this.showColorForm = !this.showColorForm;
  }

  mostrarModalDetalles(rowData, modal) {
    let vehiculo = rowData.uuid;
    this.modal = this.modalService.open(modal, {ariaLabelledBy: "modal-basic-title", size: 'xl'});

    this.empresaService.obtenerVehiculoPorUuid(this.uuid, vehiculo).subscribe((data: Vehiculo) => {
      this.vehiculo = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion del vehiculo. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  consultarSerieVehiculo(event) {
    let existeVehiculo: ExisteVehiculo = new ExisteVehiculo();
    existeVehiculo.numeroSerie = event.value;

    this.validacionService.validarVehiculo(existeVehiculo).subscribe((data: ExisteVehiculo) => {
      this.existeVehiculo = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el vehiculo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  consultarPlacasVehiculo(event) {
    let existeVehiculo: ExisteVehiculo = new ExisteVehiculo();
    existeVehiculo.placas = event.value;

    this.validacionService.validarVehiculo(existeVehiculo).subscribe((data: ExisteVehiculo) => {
      this.existeVehiculo = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el vehiculo. Motivo: ${error}`,
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
    });

    // Obteniendo la informacion
    this.vehiculosService.obtenerVehiculosMarcas().subscribe((data: VehiculoMarca[]) => {
      this.marcas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion de las marcas. ${error}`,
        ToastType.ERROR
      );
    })

    // Obteniendo el resto de informacion
    this.vehiculosService.obtenerVehiculosUsos().subscribe((data: VehiculoUso[]) => {
      this.usos = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion de los usos de vehiculos. Motivo: ${error}`,
        ToastType.ERROR
      )
    })

    this.vehiculosService.obtenerVehiculosTipos().subscribe((data: VehiculoTipo[]) => {
      this.tipos = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion de los tipos de vehiculo. ${error}`,
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
      case "COLORES":
        let formValue: Vehiculo = form.value;

        if(formValue.fechaInicio !== undefined && formValue.fechaFin !== undefined) {
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

        formValue.marca = this.marcas.filter(x => x.uuid === form.value.marca)[0];
        formValue.submarca = this.submarcas.filter(x => x.uuid === form.value.submarca)[0];
        formValue.tipo = this.tipos.filter(x => x.uuid === form.value.tipo)[0];
        formValue.uso = this.usos.filter(x => x.uuid === form.value.uso)[0];

        if(this.blindado) {
          console.log("Agregar validaciones para blindaje");
        } else {
          formValue.nivelBlindaje = null
        }

        this.toastService.showGenericToast(
          "Espere un momento",
          "Estamos guardando el vehiculo en la base de datos",
          ToastType.INFO
        );

        this.empresaService.guardarVehiculo(this.uuid, formValue).subscribe((data: Vehiculo) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se ha guardado el vehiculo con exito",
            ToastType.SUCCESS
          );
          this.vehiculo = data;
          this.stepper.next();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido guardar el vehiculo. Motivo: ${error}`,
            ToastType.ERROR
          )
        });
        break;
      case "FOTOGRAFIAS":

        break;
      case "RESUMEN":

        break;
    }
  }

  previous() {
    this.stepper.previous()
  }

  guardarColorCrear(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario es invalido. Favor de verificarlo",
        ToastType.WARNING
      );
      return;
    }

    let color: VehiculoColor = form.value;
    this.coloresTemp.push(color);
    form.reset();
  }

  eliminarColorCrear(index) {
    this.coloresTemp.splice(index, 1);
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


  guardarVehiculo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que necesitan ser rellenados",
        ToastType.WARNING
      );
      return;
    }
  }

  isColumnListed(field: string ) {
    return this.columnDefs.filter(s => s.field === field)[0] !== undefined;
  }

  guardarColor(form) {
    console.log(form.value);
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han llenado",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el color del coche",
      ToastType.INFO
    );

    let formValue: VehiculoColor = form.value;

    this.empresaService.guardarVehiculoColor(this.uuid, this.vehiculo.uuid, formValue).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el color con exito",
        ToastType.SUCCESS
      );
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el color del vehiculo. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
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
      "Estamos guardando la fotografia del vehiculo",
      ToastType.INFO
    );

    let formValue = form.value;
    let formData = new FormData();
    formData.append('fotografia', this.tempFile, this.tempFile.name);
    formData.append('metadataArchivo', JSON.stringify(formValue));

    this.empresaService.guardarVehiculoFotografia(this.uuid, this.vehiculo.uuid, formData).subscribe((data) => {
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

  mostrarFormularioNuevaFotografia() {
    this.showFotografiaForm = !this.showFotografiaForm;
  }

  descargarFotografia(uuid) {
    this.empresaService.descargarPersonaFotografia(this.uuid, this.vehiculo.uuid, uuid).subscribe((data) => {
      // @ts-ignore
      this.convertirImagen(data);
      this.modalService.open(this.mostrarFotoVehiculoModal);
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la fotografia de la persona`,
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
