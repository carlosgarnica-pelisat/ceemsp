import {Component, OnInit, ViewChild} from '@angular/core';
import {ToastService} from "../../../_services/toast.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {EmpresaService} from "../../../_services/empresa.service";
import EmpresaDomicilio from "../../../_models/EmpresaDomicilio";
import {ActivatedRoute} from "@angular/router";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ToastType} from "../../../_enums/ToastType";
import {faCheck, faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import {EstadosService} from "../../../_services/estados.service";
import {CalleService} from "../../../_services/calle.service";
import Estado from "../../../_models/Estado";
import Municipio from "../../../_models/Municipio";
import Calle from "../../../_models/Calle";
import Colonia from "../../../_models/Colonia";
import Localidad from "../../../_models/Localidad";
import {
  BotonEmpresaDomiciliosComponent
} from "../../../_components/botones/boton-empresa-domicilios/boton-empresa-domicilios.component";
import Empresa from "../../../_models/Empresa";
import {AgmGeocoder} from "@agm/core";
import EmpresaDomicilioTelefono from "../../../_models/EmpresaDomicilioTelefono";
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

  mostrandoEliminados: boolean = false;

  tempFile;

  uuid: string;
  domicilios: EmpresaDomicilio[] = [];
  domiciliosEliminados: EmpresaDomicilio[] = [];

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

  temporaryUuid: string;

  columnDefs = [
    {headerName: 'Nombre', field: 'nombre', sortable: true, filter: true },
    {headerName: 'Calle', field: 'domicilio1', sortable: true, filter: true},
    {headerName: 'No. Exterior', field: 'numeroExterior', sortable: true, filter: true},
    {headerName: 'No. Interior', field: 'numeroInterior', sortable: true, filter: true},
    {headerName: 'Colonia', field: 'domicilio2', sortable: true, filter: true},
    {headerName: 'C.P.', field: 'codigoPostal', sortable: true, filter: true},
    {headerName: 'Municipio', field: 'domicilio3', sortable: true, filter: true},
    {headerName: 'Estado', field: 'estado', sortable: true, filter: true},
    {headerName: 'Acciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this),
        editar: this.editar.bind(this),
        eliminar: this.eliminar.bind(this)
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

  constructor(private toastService: ToastService, private formbuilder: FormBuilder,
              private empresaService: EmpresaService, private route: ActivatedRoute,
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


    this.empresaService.obtenerPorUuid(this.uuid).subscribe((data: Empresa) => {
      this.empresa = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })

    this.modificarDomicilioForm = this.formbuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      matriz: ['', Validators.required],
      numeroExterior: ['', [Validators.required, Validators.maxLength(20)]],
      numeroInterior: ['', [Validators.maxLength(20)]],
      domicilio4: [''],
      codigoPostal: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(5)]],
      pais: ['Mexico', [Validators.required, Validators.maxLength(100)]],
      telefonoFijo: [''],
      telefonoMovil: ['']
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
      telefonoFijo: [''],
      telefonoMovil: ['']
    })

    this.motivosEliminacionForm = this.formbuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: [''],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.empresaService.obtenerDomicilios(this.uuid).subscribe((data: EmpresaDomicilio[]) => {
      this.rowData = data;
      this.domicilios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios. Motivo: ${error}`,
        ToastType.ERROR
      )
    })

    this.empresaService.obtenerDomiciliosEliminados(this.uuid).subscribe((data: EmpresaDomicilio[]) => {
      this.domiciliosEliminados = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los domicilios eliminados. Motivo: ${error}`,
        ToastType.ERROR
      );
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
        `No se pudieron descargar los clientes. Motivo: ${error}`,
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
    this.empresaService.obtenerDomicilioPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: EmpresaDomicilio) => {
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

  eliminar(rowData) {
    this.empresaService.obtenerDomicilioPorUuid(this.uuid, rowData.rowData?.uuid).subscribe((data: EmpresaDomicilio) => {
      this.domicilio = data;
      this.mostrarEliminarEmpresaModal();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar el domicilio. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  cambiarPestana(pestana) {
    this.pestanaActual = pestana;
  }

  mostrarEliminados() {
    this.mostrandoEliminados = true;
    this.rowData = this.domiciliosEliminados;
  }

  ocultarEliminados() {
    this.mostrandoEliminados = false;
    this.rowData = this.domicilios;
  }

  mostrarDetalles(rowData, modal) {
    let uuid = rowData.uuid;
    this.empresaService.obtenerDomicilioPorUuid(this.uuid, uuid).subscribe((data: EmpresaDomicilio) => {
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

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
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
          console.log(response);
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

  modificarDomicilio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos sin rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    if(this.estado === undefined || this.municipio === undefined || this.localidad === undefined || this.colonia === undefined || this.calle === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Alguno de los campos catalogo a llenar no esta lleno",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando los cambios en el domicilio",
      ToastType.INFO
    );

    let domicilio: EmpresaDomicilio = form.value;
    domicilio.estadoCatalogo = this.estado;
    domicilio.municipioCatalogo = this.municipio;
    domicilio.localidadCatalogo = this.localidad;
    domicilio.coloniaCatalogo = this.colonia;
    domicilio.calleCatalogo = this.calle;

    if(this.geocodeResult !== undefined) {
      domicilio.latitud = this.geocodeResult.geometry.location.lat().toString()
      domicilio.longitud = this.geocodeResult.geometry.location.lng().toString()
    }

    this.empresaService.modificarDomicilio(this.uuid, this.domicilio.uuid, domicilio).subscribe((response) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado el domicilio con exito",
        ToastType.SUCCESS
      );
      if(this.editandoModal) {
        this.modal.close();
        this.empresaService.obtenerDomicilios(this.uuid).subscribe((data: EmpresaDomicilio[]) => {
          this.rowData = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los domicilios. Motivo: ${error}`,
            ToastType.ERROR
          );

        })
        this.empresaService.obtenerDomicilioPorUuid(this.uuid, this.domicilio.uuid).subscribe((data: EmpresaDomicilio) => {
          this.domicilio = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido obtener el domicilio. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      } else {
        window.location.reload();
      }
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar el domicilio. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
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

  guardarDomicilio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay camops del formulario requeridos que no tienen informacion",
        ToastType.WARNING
      );
      return;
    }

    // Checar catalogos
    if(this.estado === undefined || this.municipio === undefined || this.localidad === undefined || this.colonia === undefined || this.calle === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Alguno de los campos catalogo a llenar no esta lleno",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando el nuevo domicilio",
      ToastType.INFO
    );

    let domicilio: EmpresaDomicilio = form.value;
    domicilio.estadoCatalogo = this.estado;
    domicilio.municipioCatalogo = this.municipio;
    domicilio.localidadCatalogo = this.localidad;
    domicilio.coloniaCatalogo = this.colonia;
    domicilio.calleCatalogo = this.calle;

    if(this.geocodeResult !== undefined) {
      domicilio.latitud = this.geocodeResult.geometry.location.lat().toString()
      domicilio.longitud = this.geocodeResult.geometry.location.lng().toString()
    }

    this.empresaService.guardarDomicilio(this.uuid, domicilio).subscribe((data: EmpresaDomicilio) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la el domicilio con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la empresa. Motivo: ${error}`,
        ToastType.ERROR
      )
    })
  }

  mostrarModificarDomicilioModal() {
    this.editandoModal = true;

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
  }

  mostrarEliminarEmpresaModal() {
    this.modalService.dismissAll();

    this.modalService.open(this.eliminarDomicilioModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  confirmarEliminarDomicilio(form) {

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
      "Estamos eliminando el domicilio",
      ToastType.INFO
    );

    let formValue: EmpresaDomicilio = form.value;

    let formData = new FormData();
    formData.append('domicilio', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaService.eliminarDomicilio(this.uuid, this.domicilio.uuid, formData).subscribe(() => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el domicilio con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido elimimar el domicilio. Motivo: ${error}`,
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

  mostrarAgregarTelefonoForm() {
    this.showTelefonoForm = !this.showTelefonoForm;

    if(!this.showTelefonoForm) {
      this.crearTelefonoForm.reset();
    }

    if(this.editandoTelefono) {
      this.editandoTelefono = false;
      //this.coloresTemp.push(this.colorVehiculo);
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
      this.empresaService.modificarTelefonoDomicilio(this.uuid, this.domicilio?.uuid, this.domicilioTelefono?.uuid, domicilio).subscribe((data: EmpresaDomicilioTelefono) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se han guardado los cambios con exito`,
          ToastType.SUCCESS
        );
        this.mostrarAgregarTelefonoForm();
        this.empresaService.obtenerTelefonosPorDomicilio(this.uuid, this.domicilio?.uuid).subscribe((data: EmpresaDomicilioTelefono[]) => {
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
      this.empresaService.guardarTelefonoDomicilio(this.uuid, this.domicilio?.uuid, domicilio).subscribe((data: EmpresaDomicilioTelefono) => {
        this.toastService.showGenericToast(
          "Listo",
          `Se ha guardado el telefono con exito`,
          ToastType.SUCCESS
        )
        this.mostrarAgregarTelefonoForm();
        this.empresaService.obtenerTelefonosPorDomicilio(this.uuid, this.domicilio?.uuid).subscribe((data: EmpresaDomicilioTelefono[]) => {
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

  mostrarEditarTelefonoForm(index) {
    this.domicilioTelefono = this.domicilio.telefonos[index];
    this.domicilio.telefonos.splice(index, 1);
    this.mostrarAgregarTelefonoForm();
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

  confirmarEliminarTelefono() {
    this.toastService.showGenericToast(
      "Espere un momento",
      `Estamos eliminando el telefono`,
      ToastType.INFO
    );

    this.empresaService.eliminarTelefonoDomicilio(this.uuid, this.domicilio?.uuid, this.temporaryUuid).subscribe((data: EmpresaDomicilioTelefono) => {
      this.toastService.showGenericToast(
        "Listo",
        `Se ha eliminado el telefono con exito`,
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaService.obtenerTelefonosPorDomicilio(this.uuid, this.domicilio.uuid).subscribe((data: EmpresaDomicilioTelefono[]) => {
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
