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
import {faCheck, faDownload, faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import ExisteVehiculo from "../../../_models/ExisteVehiculo";
import {ValidacionService} from "../../../_services/validacion.service";
import VehiculoFotografiaMetadata from "../../../_models/VehiculoFotografiaMetadata";
import {
  BotonEmpresaVehiculosComponent
} from "../../../_components/botones/boton-empresa-vehiculos/boton-empresa-vehiculos.component";
import Persona from "../../../_models/Persona";
import Empresa from "../../../_models/Empresa";

@Component({
  selector: 'app-empresa-vehiculos',
  templateUrl: './empresa-vehiculos.component.html',
  styleUrls: ['./empresa-vehiculos.component.css']
})
export class EmpresaVehiculosComponent implements OnInit {

  fechaDeHoy = new Date().toISOString().split('T')[0];

  private gridApi;
  private gridColumnApi;

  editandoModal: boolean = false;
  mostrandoEliminados: boolean = false;

  columnDefs =  [
    {headerName: 'ID', field: 'uuid', sortable: true, filter: true},
    {headerName: 'Tipo', field: 'tipo.nombre', sortable: true, filter: true },
    {headerName: 'Marca', field: 'marca.nombre', sortable: true, filter: true},
    {headerName: 'Submarca', field: 'submarca.nombre', sortable: true, filter: true},
    {headerName: 'Placas', field: 'placas', sortable: true, filter: true},
    {headerName: 'Serie', field: 'serie', sortable: true, filter: true},
    {headerName: 'Acciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        editar: this.editar.bind(this),
        eliminar: this.eliminar.bind(this)
      }}
  ];
  allColumnDefs = Vehiculo.obtenerTodasLasColumnas();

  rowData = [];
  vehiculo: Vehiculo;

  tipo: VehiculoTipo;

  vehiculos: Vehiculo[] = [];
  vehiculosEliminados: Vehiculo[] = [];

  showColorForm: boolean = false;
  showFotografiaForm: boolean = false;

  vehiculoGuardado: boolean = false;
  coloresGuardados: boolean = false;

  pestanaActual: string = "DETALLES";

  uuid: string;
  empresa: Empresa;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearVehiculoForm: FormGroup;
  crearColorForm: FormGroup;
  crearVehiculoFotografiaForm: FormGroup;
  motivosEliminacionForm: FormGroup;

  marca: VehiculoMarca;
  marcas: VehiculoMarca[];
  submarcas: VehiculoSubmarca[];
  tipos: VehiculoTipo[];
  domicilios: EmpresaDomicilio[] = [];
  usos: VehiculoUso[] = [];
  personal: Persona[];

  blindado: boolean = false;
  origen: string = "";

  stepper: Stepper;

  faCheck = faCheck;
  faTrash = faTrash;
  faDownload = faDownload;
  faPencilAlt = faPencilAlt;

  imagenActual;
  tempFile;
  pdfActual;

  existeVehiculo: ExisteVehiculo;

  tempUuidColorVehiculo: string;
  tempUuidFotografiaVehiculo: string;

  colorVehiculo: VehiculoColor;

  editandoColor: boolean;

  temporaryIndex: number;

  coloresTemp: VehiculoColor[] = [];
  color: VehiculoColor;

  @ViewChild('mostrarDetallesVehiculoModal') mostrarDetallesVehiculoModal: any;
  @ViewChild('mostrarFotoVehiculoModal') mostrarFotoVehiculoModal: any;
  @ViewChild('eliminarVehiculoColorModal') eliminarVehiculoColorModal: any;
  @ViewChild('eliminarVehiculoFotografiaModal') eliminarVehiculoFotografiaModal: any;
  @ViewChild('eliminarVehiculoModal') eliminarVehiculoModal: any;
  @ViewChild('quitarVehiculoColorModal') quitarVehiculoColorModal: any;
  @ViewChild('modificarVehiculoModal') modificarVehiculoModal: any;
  @ViewChild('visualizarConstanciaBlindajeModal') visualizarConstanciaBlindajeModal;

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData, this.mostrarDetallesVehiculoModal)
  }

  constructor(private modalService: NgbModal, private toastService: ToastService,
              private empresaService: EmpresaService, private formBuilder: FormBuilder,
              private vehiculosService: VehiculosService, private route: ActivatedRoute,
              private validacionService: ValidacionService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaVehiculosComponent
    }

    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.empresaService.obtenerPorUuid(this.uuid).subscribe((data: Empresa) => {
      this.empresa = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.crearVehiculoForm = this.formBuilder.group({
      placas: ['', [Validators.required, Validators.maxLength(15)]],
      serie: ['', [Validators.required, Validators.maxLength(30)]],
      tipo: ['', Validators.required],
      marca: ['', Validators.required],
      submarca: [''],
      anio: ['', Validators.required],
      rotulado: ['', Validators.required],
      uso: ['', Validators.required],
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
      fechaFin: [''],
      domicilio: ['', Validators.required]
      // TODO: Agregar campos para fotos y documentos; asi como constancia de blindaje
    })

    this.crearColorForm = this.formBuilder.group({
      color: ['', Validators.required],
      descripcion: ['', [Validators.required, Validators.maxLength(100)]]
    })

    this.crearVehiculoFotografiaForm = this.formBuilder.group({
      file: ['', Validators.required],
      descripcion: ['', Validators.required]
    });

    this.motivosEliminacionForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      fechaBaja: ['', Validators.required],
      observacionesBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.empresaService.obtenerVehiculos(this.uuid).subscribe((data: Vehiculo[]) => {
      this.rowData = data;
      this.vehiculos = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los vehiculos. ${error}`,
        ToastType.ERROR
      )
    });

    this.empresaService.obtenerPersonal(this.uuid).subscribe((data: Persona[]) => {
      this.personal = data
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar las personas. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.empresaService.obtenerVehiculosEliminados(this.uuid).subscribe((data: Vehiculo[]) => {
      this.vehiculosEliminados = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los vehiculos eliminados. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

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

  mostrarModalEliminarFotografia(uuid) {
    this.tempUuidFotografiaVehiculo = uuid;
    this.modal = this.modalService.open(this.eliminarVehiculoFotografiaModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
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

  convertirPdf(pdf: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.pdfActual = reader.result;
    });

    if(pdf) {
      reader.readAsDataURL(pdf);
    }
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

  editar(rowData) {
    if(rowData.rowData?.eliminado) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El elemento ya esta eliminado. No se puede editar`,
        ToastType.WARNING
      );
      return;
    }

    this.empresaService.obtenerVehiculoPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: Vehiculo) => {
      this.vehiculo = data;
      this.editandoModal = false;

      let marca = this.marcas.filter(x => x.uuid === this.vehiculo.marca.uuid)[0];
      this.vehiculosService.obtenerVehiculoMarcaPorUuid(marca.uuid).subscribe((data: VehiculoMarca) => {
        this.marca = data;
        this.submarcas = data.submarcas;

        this.crearVehiculoForm.patchValue({
          placas: this.vehiculo.placas,
          serie: this.vehiculo.serie,
          tipo: this.vehiculo.tipo.uuid,
          marca: this.vehiculo.marca.uuid,
          submarca: this.vehiculo.submarca.uuid,
          anio: this.vehiculo.anio,
          rotulado: this.vehiculo.rotulado,
          uso: this.vehiculo.uso.uuid,
          origen: this.vehiculo.origen,
          blindado: this.vehiculo.blindado,
          serieBlindaje: this.vehiculo.serieBlindaje,
          fechaBlindaje: this.vehiculo.fechaBlindaje,
          numeroHolograma: this.vehiculo.numeroHolograma,
          placaMetalica: this.vehiculo.placaMetalica,
          empresaBlindaje: this.vehiculo.empresaBlindaje,
          nivelBlindaje: this.vehiculo.nivelBlindaje,
          razonSocial: this.vehiculo.razonSocial,
          fechaInicio: this.vehiculo.fechaInicio,
          fechaFin: this.vehiculo.fechaFin,
          domicilio: this.vehiculo.domicilio.uuid
        });

        this.blindado = this.vehiculo.blindado;
        this.origen = this.vehiculo.origen;

        this.modal = this.modalService.open(this.modificarVehiculoModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl', backdrop: "static"});

        this.modal.result.then((result) => {
          this.closeResult = `Closed with ${result}`;
        }, (error) => {
          this.closeResult = `Dismissed ${this.getDismissReason(error)}`
        });
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido descargar la informacion de la marca. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el vehiculo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  eliminar(rowData) {
    if(rowData.rowData?.eliminado) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El elemento ya esta eliminado. No se puede eliminar`,
        ToastType.WARNING
      );
      return;
    }

    this.empresaService.obtenerVehiculoPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: Vehiculo) => {
      this.vehiculo = data;
      this.mostrarModalEliminarVehiculo();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el vehiculo. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarEliminados() {
    this.mostrandoEliminados = true;
    this.rowData = this.vehiculosEliminados;
  }

  ocultarEliminados() {
    this.mostrandoEliminados = false;
    this.rowData = this.vehiculos;
  }

  descargarMarcasPorTipo(event) {
    let tipoUuid = event.value;
    this.tipo = this.tipos.filter(x => x.uuid === tipoUuid)[0];

    if(this.tipo?.nombre === 'MOTOCICLETA' || this.tipo?.nombre === 'CUATRIMOTO') {
      this.crearVehiculoForm.controls["submarca"].disable();
    } else {
      this.crearVehiculoForm.controls["submarca"].enable();
    }

    this.vehiculosService.obtenerVehiculosTiposMarca(this.tipo.tipo).subscribe((data: VehiculoMarca[]) => {
      this.marcas = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las marcas por tipo. Motivo: ${error}`,
        ToastType.ERROR
      );
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

  mostrarEditarColorForm(index) {
    this.colorVehiculo = this.coloresTemp[index];
    this.coloresTemp.splice(index, 1);
    this.mostrarFormularioColor();
    this.editandoColor = true;
    this.crearColorForm.patchValue({
      color: this.colorVehiculo.color,
      descripcion: this.colorVehiculo.descripcion
    })
  }

  mostrarFormularioColor() {
    this.showColorForm = !this.showColorForm;

    if(!this.showColorForm) {
      this.crearColorForm.reset();
    }

    if(this.editandoColor) {
      this.editandoColor = false;
      this.coloresTemp.push(this.colorVehiculo);
    }
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

  cancelarCambiosVehiculo() {
    this.crearVehiculoForm.reset();
    this.modal.close();
  }

  mostrarModalModificarVehiculo() {
    this.editandoModal = true;
    let marca = this.marcas.filter(x => x.uuid === this.vehiculo.marca.uuid)[0];
    this.vehiculosService.obtenerVehiculoMarcaPorUuid(marca.uuid).subscribe((data: VehiculoMarca) => {
      this.marca = data;
      this.tipo = this.vehiculo.tipo;
      this.blindado = this.vehiculo?.blindado;
      this.origen = this.vehiculo?.origen;
      this.submarcas = data.submarcas;

      if(this.tipo?.nombre === 'MOTOCICLETA' || this.tipo?.nombre === 'CUATRIMOTO') {
        this.crearVehiculoForm.controls["submarca"].disable();
      } else {
        this.crearVehiculoForm.controls["submarca"].enable();
      }

      this.crearVehiculoForm.patchValue({
        placas: this.vehiculo.placas,
        serie: this.vehiculo.serie,
        tipo: this.vehiculo.tipo.uuid,
        marca: this.vehiculo.marca.uuid,
        submarca: this.vehiculo?.submarca?.uuid,
        anio: this.vehiculo.anio,
        rotulado: this.vehiculo.rotulado,
        uso: this.vehiculo.uso.uuid,
        origen: this.vehiculo.origen,
        blindado: this.vehiculo.blindado,
        serieBlindaje: this.vehiculo.serieBlindaje,
        fechaBlindaje: this.vehiculo.fechaBlindaje,
        numeroHolograma: this.vehiculo.numeroHolograma,
        placaMetalica: this.vehiculo.placaMetalica,
        empresaBlindaje: this.vehiculo.empresaBlindaje,
        nivelBlindaje: this.vehiculo.nivelBlindaje,
        razonSocial: this.vehiculo.razonSocial,
        fechaInicio: this.vehiculo.fechaInicio,
        fechaFin: this.vehiculo.fechaFin,
        domicilio: this.vehiculo.domicilio.uuid
      });

      this.modal = this.modalService.open(this.modificarVehiculoModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl', backdrop: "static"});

      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      });
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la marca. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  guardarCambiosVehiculo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El formulario no es valido.`,
        ToastType.WARNING
      );
      return;
    }

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
    formValue.domicilio = this.domicilios.filter(x => x.uuid === form.value.domicilio)[0];

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

    let vehiculo = new FormData();
    if(this.tempFile !== undefined) {
      vehiculo.append('constanciaBlindaje', this.tempFile, this.tempFile.name);
    } else {
      vehiculo.append('constanciaBlindaje', null)
    }
    vehiculo.append('vehiculo', JSON.stringify(formValue));

    this.empresaService.modificarVehiculo(this.uuid, this.vehiculo.uuid, vehiculo).subscribe((data: Vehiculo) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado el vehiculo con exito",
        ToastType.SUCCESS
      );
      if(this.editandoModal) {
        this.modal.close();
        this.empresaService.obtenerVehiculoPorUuid(this.uuid, this.vehiculo.uuid).subscribe((data: Vehiculo) => {
          this.vehiculo = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar el vehiculo. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      } else {
        window.location.reload();
      }
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar el vehiculo. Motivo: ${error}`,
        ToastType.ERROR
      )
    });
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
  }

  next(stepName: string, form) {
    if(!form.valid && stepName !== "FOTOGRAFIAS") {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenarse",
        ToastType.WARNING
      );
      return;
    }
    switch (stepName) {
      case "COLORES":
        if(this.vehiculoGuardado) {
          this.stepper.next();
        } else {
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
          formValue.domicilio = this.domicilios.filter(x => x.uuid === form.value.domicilio)[0];

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

          let vehiculo = new FormData();
          if(this.tempFile !== undefined) {
            vehiculo.append('constanciaBlindaje', this.tempFile, this.tempFile.name);
          } else {
            vehiculo.append('constanciaBlindaje', null)
          }
          vehiculo.append('vehiculo', JSON.stringify(formValue));

          this.empresaService.guardarVehiculo(this.uuid, vehiculo).subscribe((data: Vehiculo) => {
            this.toastService.showGenericToast(
              "Listo",
              "Se ha guardado el vehiculo con exito",
              ToastType.SUCCESS
            );
            console.log(data);
            this.vehiculo = data;
            this.vehiculoGuardado = true;
            this.stepper.next();
          }, (error) => {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              `No se ha podido guardar el vehiculo. Motivo: ${error}`,
              ToastType.ERROR
            )
          });
        }
        break;
      case "RESUMEN":
        if(this.vehiculo?.fotografias?.length < 1) {
          this.toastService.showGenericToast(
            `Espera un momento`,
            `No se ha guardado ninguna fotografia`,
            ToastType.WARNING
          );
          return;
        }
        this.stepper.next();
        break;
      case "FOTOGRAFIAS":
        if(this.coloresGuardados) {
          this.stepper.next();
        } else {
          if(this.coloresTemp.length < 1) {
            this.toastService.showGenericToast(
              "Ocurrio un problema",
              "Necesitas agregar por lo menos un color para continuar con el alta del vehiculo",
              ToastType.WARNING
            );
            return;
          }

          this.toastService.showGenericToast(
            "Espere un momento",
            "Estamos guardando los colores del vehiculo",
            ToastType.INFO
          );

          let coloresGuardados: boolean = true;

          this.coloresTemp.forEach(c => {
            this.empresaService.guardarVehiculoColor(this.uuid, this.vehiculo.uuid, c).subscribe((data) => {
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
              coloresGuardados = false;
            });
          })

          if(coloresGuardados) {
            this.coloresGuardados = true;
            this.stepper.next();
          }
        }
        break;
    }
  }

  finalizar() {
    window.location.reload();
  }

  mostrarModalEliminar(modal, temporaryIndex) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});
    this.temporaryIndex = temporaryIndex;
  }

  mostrarModalEliminarVehiculo() {
    this.modal = this.modalService.open(this.eliminarVehiculoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  confirmarEliminarVehiculo(form) {
    console.log(form.value);
    console.log(form.valid);
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
      "Se esta eliminando el vehiculo",
      ToastType.INFO
    );

    let formValue: Vehiculo = form.value;

    let formData = new FormData();
    formData.append('vehiculo', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaService.eliminarVehiculo(this.uuid, this.vehiculo.uuid, formData).subscribe(() => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el vehiculo con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El vehiculo no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  previous() {
    this.stepper.previous()
  }

  mostrarEditarVehiculoColor(index) {
    this.colorVehiculo = this.vehiculo.colores[index];
    this.mostrarFormularioColor();
    this.editandoColor = true;
    this.crearColorForm.patchValue({
      color: this.colorVehiculo.color,
      descripcion: this.colorVehiculo.descripcion
    })
  }

  mostrarModalEliminarVehiculoColor(uuid) {
    this.tempUuidColorVehiculo = uuid;
    this.modal = this.modalService.open(this.eliminarVehiculoColorModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
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
    this.editandoColor = false;
    this.showColorForm = false;
  }

  mostrarModalQuitarVehiculoColor(index) {
    this.temporaryIndex = index;
    this.modal = this.modalService.open(this.quitarVehiculoColorModal, {size: 'lg'})
  }

  quitarColor() {
    this.coloresTemp.splice(this.temporaryIndex, 1);
    this.modal.close();
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

  confirmarEliminarVehiculoColor() {
    if(this.tempUuidColorVehiculo === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del color del vehiculo no esta asignado",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el color del vehiculo",
      ToastType.INFO
    );

    this.empresaService.eliminarVehiculoColor(this.uuid, this.vehiculo.uuid, this.tempUuidColorVehiculo).subscribe((data: VehiculoColor) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el color con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerVehiculoColores(this.uuid, this.vehiculo.uuid).subscribe((data: VehiculoColor[]) => {
        this.vehiculo.colores = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los colores. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El color del vehiculo no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarVehiculoFotografia() {
    if(this.tempUuidFotografiaVehiculo === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID de la fotografia del vehiculo no esta asignado",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando la fotografia del vehiculo",
      ToastType.INFO
    );

    this.empresaService.eliminarVehiculoFotografia(this.uuid, this.vehiculo.uuid, this.tempUuidFotografiaVehiculo).subscribe((data) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el color con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.listarVehiculoFotografias(this.uuid, this.vehiculo.uuid).subscribe((data: VehiculoFotografiaMetadata[]) => {
        this.vehiculo.fotografias = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las fotografias. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El color del vehiculo no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  isColumnListed(field: string ) {
    return this.columnDefs.filter(s => s.field === field)[0] !== undefined;
  }

  guardarColor(form) {
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

    if(this.editandoColor) {
      formValue.id = this.colorVehiculo.id;
      formValue.uuid = this.colorVehiculo.uuid;
      this.empresaService.modificarVehiculoColor(this.uuid, this.vehiculo.uuid, this.colorVehiculo.uuid, formValue).subscribe((data: VehiculoColor) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se guardo el color del carro con exito",
          ToastType.INFO
        );
        this.mostrarFormularioColor();
        this.empresaService.obtenerVehiculoColores(this.uuid, this.vehiculo.uuid).subscribe((data: VehiculoColor[]) => {
          this.vehiculo.colores = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los colores. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el color del vehiculo. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.empresaService.guardarVehiculoColor(this.uuid, this.vehiculo.uuid, formValue).subscribe((data) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado el color con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioColor();
        this.empresaService.obtenerVehiculoColores(this.uuid, this.vehiculo.uuid).subscribe((data: VehiculoColor[]) => {
          this.vehiculo.colores = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los colores. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el color del vehiculo. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
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
      this.mostrarFormularioNuevaFotografia();
      this.empresaService.listarVehiculoFotografias(this.uuid, this.vehiculo.uuid).subscribe((data: VehiculoFotografiaMetadata[]) => {
        this.vehiculo.fotografias = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las fotografias. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
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
    this.empresaService.descargarVehiculoFotografia(this.uuid, this.vehiculo.uuid, uuid).subscribe((data) => {
      // @ts-ignore
      this.convertirImagen(data);
      this.modalService.open(this.mostrarFotoVehiculoModal);
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la fotografia del vehiculo`,
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

  mostrarModalVerConstanciaBlindaje() {
    this.modal = this.modalService.open(this.visualizarConstanciaBlindajeModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.empresaService.descargarVehiculoConstanciaPdf(this.uuid, this.vehiculo?.uuid).subscribe((data: Blob) => {
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
