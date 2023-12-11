import {Component, OnInit, ViewChild} from '@angular/core';
import {ToastService} from "../../_services/toast.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {EmpresaService} from "../../_services/empresa.service";
import EmpresaDomicilio from "../../_models/EmpresaDomicilio";
import {ActivatedRoute} from "@angular/router";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ToastType} from "../../_enums/ToastType";
import {faCheck, faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import {DomiciliosService} from "../../_services/domicilios.service";
import Estado from "../../_models/Estado";
import Municipio from "../../_models/Municipio";
import Calle from "../../_models/Calle";
import Colonia from "../../_models/Colonia";
import Localidad from "../../_models/Localidad";
import {EstadosService} from "../../_services/estados.service";
import {CalleService} from "../../_services/calle.service";
import EmpresaDomicilioTelefono from "../../_models/EmpresaDomicilioTelefono";
import Empresa from "../../_models/Empresa";
import {
  BotonEmpresaDomiciliosComponent
} from "../../_components/botones/boton-empresa-domicilios/boton-empresa-domicilios.component";
import {Table} from "primeng/table";
import { AgmGeocoder } from '@agm/core';
import GeocoderResult = google.maps.GeocoderResult;

@Component({
  selector: 'app-empresa-domicilios',
  templateUrl: './empresa-domicilios.component.html',
  styleUrls: ['./empresa-domicilios.component.css']
})
export class EmpresaDomiciliosComponent implements OnInit {

  editandoModal: boolean = false;
  faCheck = faCheck;
  faPencil = faPencilAlt;
  faTrash = faTrash;

  estados: Estado[] = [];
  municipios: Municipio[] = [];
  calles: Calle[] = [];
  colonias: Colonia[] = [];
  localidades: Localidad[] = [];

  temporaryUuid: string;

  tempFile;

  uuid: string;
  domicilios: EmpresaDomicilio[];

  nuevoDomicilioForm: FormGroup;
  modificarDomicilioForm: FormGroup;
  estadoSearchForm: FormGroup;
  municipioSearchForm: FormGroup;
  localidadSearchForm: FormGroup;
  calleSearchForm: FormGroup;
  coloniaSearchForm: FormGroup;
  motivosEliminacionForm: FormGroup;
  crearTelefonoForm: FormGroup;

  modal: NgbModalRef;
  closeResult: string;

  domicilioTelefono: EmpresaDomicilioTelefono;

  private gridApi;
  private gridColumnApi;

  geocodeResult: GeocoderResult;

  domicilio: EmpresaDomicilio;
  domicilioUbicado: boolean = false;

  columnDefs = [
    {headerName: 'Tipo', field: 'tipo', sortable: true, filter: true, resizable: true, valueGetter: function(params) {
      if(params.data.matriz) {
        return "Matriz"
      } else {
        return "Sucursal"
      }
      }},
    {headerName: 'Calle', field: 'domicilio1', sortable: true, filter: true, resizable: true, width: 350, minWidth: 250, maxWidth: 450, valueGetter: function(params) {
        if(params.data.numeroInterior !== "") {
          return `${params.data.domicilio1} ${params.data.numeroExterior} Int. ${params.data?.numeroInterior}`
        } else {
          return `${params.data.domicilio1} ${params.data.numeroExterior}`
        }

      }},
    {headerName: 'Colonia', field: 'domicilio2', sortable: true, filter: true, resizable: true},
    {headerName: 'C.P.', field: 'codigoPostal', sortable: true, filter: true, resizable: true},
    {headerName: 'Municipio', field: 'domicilio3', sortable: true, filter: true, resizable: true},
    {headerName: 'Estado', field: 'estado', sortable: true, filter: true, resizable: true},
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this)
      }}
  ];
  allColumnDefs = EmpresaDomicilio.obtenerTodasLasColumnas();

  rowData = [];

  frameworkComponents: any;
  rowDataClicked = {
    uuid: undefined
  };

  estado: Estado;
  municipio: Municipio;
  localidad: Localidad;
  colonia: Colonia;
  calle: Calle;

  estadoQuery: string = '';
  municipioQuery: string = '';
  localidadQuery: string = '';
  coloniaQuery: string = '';
  calleQuery: string = '';

  fechaDeHoy = new Date().toISOString().split('T')[0];

  obtenerCallesTimeout = undefined;
  empresa: Empresa;

  pestanaActual: string = 'DETALLES';
  showTelefonoForm: boolean = false;
  editandoTelefono: boolean = false;

  @ViewChild('mostrarDetallesDomicilioModal') mostrarDetallesDomicilioModal: any;
  @ViewChild('modificarDomicilioModal') modificarDomicilioModal: any;
  @ViewChild('eliminarDomicilioModal') eliminarDomicilioModal: any;
  @ViewChild('eliminarDomicilioTelefonoModal') eliminarDomicilioTelefonoModal: any;
  @ViewChild('agregarTelefonoModal') agregarTelefonoModal: any;

  constructor(private toastService: ToastService, private formbuilder: FormBuilder,
              private domiciliosService: DomiciliosService, private route: ActivatedRoute,
              private modalService: NgbModal, private estadoService: EstadosService,
              private calleService: CalleService, private geocodeService: AgmGeocoder) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaDomiciliosComponent
    }

    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.estadoSearchForm = this.formbuilder.group({
      nombre: ['']
    });

    this.municipioSearchForm = this.formbuilder.group({
      nombre: ['']
    });

    this.localidadSearchForm = this.formbuilder.group({
      nombre: ['']
    })

    this.coloniaSearchForm = this.formbuilder.group({
      nombre: ['']
    });

    this.calleSearchForm = this.formbuilder.group({
      nombre: ['']
    })

    this.estadoSearchForm = this.formbuilder.group({
      nombre: ['']
    });

    this.modificarDomicilioForm = this.formbuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      matriz: ['', Validators.required],
      numeroExterior: ['', [Validators.required, Validators.maxLength(20)]],
      numeroInterior: ['', [Validators.maxLength(20)]],
      domicilio4: [''],
      codigoPostal: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(5)]],
      pais: ['Mexico', [Validators.required, Validators.maxLength(100)]],
      telefonoFijo: ['', [Validators.required]],
      telefonoMovil: ['', [Validators.required]]
      // TODO: Volver a agregar los campos latitud y longitud cuando se tenga la extension de google maps
    });

    this.crearTelefonoForm = this.formbuilder.group({
      tipoTelefono: ['', Validators.required],
      telefono: ['', Validators.required]
    })

    this.nuevoDomicilioForm = this.formbuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      numeroExterior: ['', [Validators.required, Validators.maxLength(20)]],
      numeroInterior: ['', [Validators.maxLength(20)]],
      domicilio4: [''],
      codigoPostal: ['', Validators.required],
      pais: ['Mexico', [Validators.required, Validators.maxLength(100)]],
      matriz: ['', Validators.required], // TODO: Quitar el si/no y agregar tipo de domicilio como matriz / sucursal
      telefonoFijo: ['', [Validators.required]],
      telefonoMovil: ['', [Validators.required]]
    })

    this.motivosEliminacionForm = this.formbuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: ['', Validators.required],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.domiciliosService.obtenerDomicilios().subscribe((data: EmpresaDomicilio[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios. Motivo: ${error}`,
        ToastType.ERROR
      )
    })

    this.estadoService.obtenerEstados().subscribe((data: Estado[]) => {
      this.estados = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los estados. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.calleService.obtenerCallesPorLimite(10).subscribe((response: Calle[]) => {
      this.calles = response;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrió un problema",
        `No se pudieron descargar las calles. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  verDetalles(rowData) {
    this.mostrarDetalles(rowData.rowData, this.mostrarDetallesDomicilioModal)
  }

  editar(rowData) {
    this.domiciliosService.obtenerDomicilioPorUuid(rowData.rowData?.uuid).subscribe((data: EmpresaDomicilio) => {
      this.domicilio = data;
      this.editandoModal = false;

      this.modificarDomicilioForm.setValue({
        nombre: this.domicilio.nombre,
        numeroExterior: this.domicilio.numeroExterior,
        numeroInterior: this.domicilio.numeroInterior,
        domicilio4: this.domicilio.domicilio4,
        codigoPostal: this.domicilio.codigoPostal,
        pais: this.domicilio.pais,
        telefonoFijo: this.domicilio.telefonoFijo,
        telefonoMovil: this.domicilio.telefonoMovil,
        matriz: this.domicilio.matriz
      });

      this.estado = this.domicilio.estadoCatalogo;
      this.municipio = this.domicilio.municipioCatalogo;
      this.calle = this.domicilio.calleCatalogo;
      this.localidad = this.domicilio.localidadCatalogo;
      this.colonia = this.domicilio.coloniaCatalogo;

      this.estadoService.obtenerEstadosPorMunicipio(this.estado.uuid).subscribe((data: Municipio[]) => {
        this.municipios = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los municipios relacionados. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.estadoService.obtenerLocalidadesPorMunicipioYEstado(this.estado.uuid, this.municipio.uuid).subscribe((data: Localidad[]) => {
        this.localidades = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las localidades. Motivo: ${error}`,
          ToastType.ERROR
        );
      });

      this.estadoService.obtenerColoniasPorMunicipioYEstado(this.estado.uuid, this.municipio.uuid).subscribe((data: Colonia[]) => {
        this.colonias = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar las colonias. Motivo: ${error}`,
          ToastType.ERROR
        );
      })

      this.modal = this.modalService.open(this.modificarDomicilioModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
      })

    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el domicilio. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  mostrarAgregarTelefonoForm() {
    this.showTelefonoForm = !this.showTelefonoForm;

    if(!this.showTelefonoForm) {
      this.crearTelefonoForm.reset();
    }

    if(this.editandoTelefono) {
      this.editandoTelefono = false;
    }
  }

  guardarTelefono(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El formulario es invalido`,
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      `Estamos guardando el telefono en el domicilio`,
      ToastType.INFO
    );

    let domicilio: EmpresaDomicilioTelefono = form.value;

    if(this.editandoTelefono) {
      this.domiciliosService.modificarTelefonoDomicilio(this.domicilio?.uuid, this.domicilioTelefono?.uuid, domicilio).subscribe((data: EmpresaDomicilioTelefono) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se han guardado los cambios con exito`,
          ToastType.SUCCESS
        );
        this.mostrarAgregarTelefonoForm();
        this.crearTelefonoForm.reset();
        this.modal.close();
        this.domiciliosService.obtenerTelefonosDomicilio(this.domicilio?.uuid).subscribe((data: EmpresaDomicilioTelefono[]) => {
          this.domicilio.telefonos = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido obtener los telefonos. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el telefono. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.domiciliosService.guardarTelefonoDomicilio(this.domicilio?.uuid, domicilio).subscribe((data: EmpresaDomicilioTelefono) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se ha guardado el telefono con exito`,
          ToastType.SUCCESS
        )
        this.mostrarAgregarTelefonoForm();
        this.crearTelefonoForm.reset();
        this.modal.close();
        this.domiciliosService.obtenerTelefonosDomicilio(this.domicilio?.uuid).subscribe((data: EmpresaDomicilioTelefono[]) => {
          this.domicilio.telefonos = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los domicilios. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el telefono. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  cambiarPestana(pestana) {
    this.pestanaActual = pestana;
  }

  mostrarDetalles(rowData, modal) {
    let uuid = rowData.uuid;
    this.domiciliosService.obtenerDomicilioPorUuid(uuid).subscribe((data: EmpresaDomicilio) => {
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
  onFileChange(event) {
    this.tempFile = event.target.files[0]
  }

  seleccionarEstado(estadoUuid) {
    this.municipio = undefined;
    this.calle = undefined;
    this.localidad = undefined;
    this.colonia = undefined;

    this.estado = this.estados.filter(x => x.uuid === estadoUuid)[0];
    this.nuevoDomicilioForm.patchValue({
      estado: this.estado.nombre
    })
    this.estadoService.obtenerEstadosPorMunicipio(estadoUuid).subscribe((data: Municipio[]) => {
      this.municipios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los municipios. Motivo: ${error}`,
        ToastType.ERROR
      )
    });
  }

  eliminarEstado() {
    this.estado = undefined;
    this.municipio = undefined;
    this.localidad = undefined;
    this.colonia = undefined;

    this.nuevoDomicilioForm.patchValue({
      estado: undefined,
      domicilio3: undefined,
      domicilio2: undefined
    })
  }

  clear(table: Table) {
    table.clear();
  }

  mostrarModalAgregarTelefono() {
    this.modal = this.modalService.open(this.agregarTelefonoModal, {size: 'xl', backdrop: 'static'})
  }

  mostrarEditarTelefonoForm(uuid) {
    this.domicilioTelefono = this.domicilio.telefonos.filter(x => x.uuid === uuid)[0];
    this.mostrarAgregarTelefonoForm();
    this.modal = this.modalService.open(this.agregarTelefonoModal, {size: 'lg', backdrop: 'static'})
    this.editandoTelefono = true;
    this.crearTelefonoForm.patchValue({
      tipoTelefono: this.domicilioTelefono.tipoTelefono,
      telefono: this.domicilioTelefono.telefono
    })
  }

  mostrarModalEliminarTelefono(uuid) {
    this.temporaryUuid = uuid;
    this.modal = this.modalService.open(this.eliminarDomicilioTelefonoModal, {size: "lg", backdrop: "static"})
  }

  seleccionarMunicipio(municipioUuid) {
    this.localidad = undefined;
    this.colonia = undefined;
    this.calle = undefined;

    this.municipio = this.municipios.filter(x => x.uuid === municipioUuid)[0];

    this.estadoService.obtenerColoniasPorMunicipioYEstado(this.estado.uuid, municipioUuid).subscribe((data: Colonia[]) => {
      this.colonias = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las colonias. Motivo: ${error}`,
        ToastType.ERROR
      );
    });

    this.estadoService.obtenerLocalidadesPorMunicipioYEstado(this.estado.uuid, municipioUuid).subscribe((data: Localidad[]) => {
      this.localidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las localidades. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  eliminarMunicipio() {
    this.municipio = undefined;
    this.localidad = undefined;
    this.colonia = undefined;
    this.calle = undefined;
  }

  seleccionarLocalidad(localidadUuid) {
    this.localidad = this.localidades.filter(x => x.uuid === localidadUuid)[0];
  }

  eliminarLocalidad() {
    this.localidad = undefined;
  }

  seleccionarColonia(coloniaUuid) {
    this.colonia = this.colonias.filter(x => x.uuid === coloniaUuid)[0];
    this.nuevoDomicilioForm.patchValue({
      codigoPostal: this.colonia.codigoPostal
    })
  }

  eliminarColonia() {
    this.colonia = undefined;
  }

  obtenerCalles(event) {
    if(this.obtenerCallesTimeout !== undefined) {
      clearTimeout(this.obtenerCallesTimeout);
    }

    this.obtenerCallesTimeout = setTimeout(() => {
      if(this.calleQuery === '' || this.calleQuery === undefined) {
        this.calleService.obtenerCallesPorLimite(10).subscribe((response: Calle[]) => {
          this.calles = response;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrió un problema",
            `No se pudieron descargar los clientes. Motivo: ${error}`,
            ToastType.ERROR
          )
        })
      } else {
        this.calleService.obtenerCallesPorQuery(this.calleQuery).subscribe((response: Calle[]) => {
          this.calles = response;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `Los clientes no se pudieron obtener. Motivo: ${error}`,
            ToastType.ERROR
          )
        });
      }
    }, 1000);
  }

  seleccionarCalle(calleUuid) {
    this.calle = this.calles.filter(x => x.uuid === calleUuid)[0];
  }

  eliminarCalle() {
    this.calle = undefined;
  }

  isColumnListed(field: string ) {
    return this.columnDefs.filter(s => s.field === field)[0] !== undefined;
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

  ubicarDomicilio(form) {
    if(!form.valid || this.estado === undefined || this.municipio === undefined || this.localidad === undefined || this.colonia === undefined || this.calle === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Favor de proporcionar mas datos para hacer la busqueda mas precisa",
        ToastType.WARNING
      );
      return;
    }

    let domicilioEmpresa: EmpresaDomicilio = form.value;
    domicilioEmpresa.estadoCatalogo = this.estado;
    domicilioEmpresa.municipioCatalogo = this.municipio;
    domicilioEmpresa.localidadCatalogo = this.localidad;
    domicilioEmpresa.coloniaCatalogo = this.colonia;
    domicilioEmpresa.calleCatalogo = this.calle;

    let query = `${domicilioEmpresa?.calleCatalogo?.nombre} ${domicilioEmpresa?.numeroExterior} ${domicilioEmpresa?.numeroInterior} ${domicilioEmpresa?.coloniaCatalogo.nombre} ${domicilioEmpresa?.municipioCatalogo?.nombre} ${domicilioEmpresa?.estadoCatalogo?.nombre}`

    this.geocodeService.geocode({
      address: query
    }).subscribe((data: GeocoderResult[]) => {
      this.geocodeResult = data[0];
      this.domicilioUbicado = true;

    }, (error) => {
      this.domicilioUbicado = false;
      this.toastService.showGenericToast(
        `Ocurrio un problema`,
        `Ocurrio un problema cuando el domicilio era ubicado en el mapa. Motivo: ${error}`,
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

  confirmarEliminarTelefono() {
    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos eliminando el telefono`,
      ToastType.INFO
    );

    this.domiciliosService.eliminarTelefonoDomicilio(this.domicilio?.uuid, this.temporaryUuid).subscribe((data: EmpresaDomicilioTelefono) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha eliminado el telefono con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.domiciliosService.obtenerTelefonosDomicilio(this.domicilio.uuid).subscribe((data: EmpresaDomicilioTelefono[]) => {
        this.domicilio.telefonos = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los domicilios. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar el telefono. Motivo: ${error}`,
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

  stringToNumber(string: String): Number {
    return Number(string)
  }

}
