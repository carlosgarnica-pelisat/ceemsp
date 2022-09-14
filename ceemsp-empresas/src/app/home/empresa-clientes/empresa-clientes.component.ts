import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ActivatedRoute} from "@angular/router";
import {ToastService} from "../../_services/toast.service";
import {EmpresaService} from "../../_services/empresa.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastType} from "../../_enums/ToastType";
import Cliente from "../../_models/Cliente";
import Stepper from "bs-stepper";
import ClienteDomicilio from "../../_models/ClienteDomicilio";
import {TipoInfraestructuraService} from "../../_services/tipo-infraestructura.service";
import TipoInfraestructura from "../../_models/TipoInfraestructura";
import {EmpresaClientesService} from "../../_services/empresa-clientes.service";
import {EstadosService} from "../../_services/estados.service";
import Estado from "../../_models/Estado";
import {CalleService} from "../../_services/calle.service";
import Calle from "../../_models/Calle";
import {faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import Municipio from "../../_models/Municipio";
import Colonia from "../../_models/Colonia";
import Localidad from "../../_models/Localidad";
import validateRfc from "validate-rfc/src";

@Component({
  selector: 'app-empresa-clientes',
  templateUrl: './empresa-clientes.component.html',
  styleUrls: ['./empresa-clientes.component.css']
})
export class EmpresaClientesComponent implements OnInit {

  uuid: string;

  faTrash = faTrash;
  faPencilAlt = faPencilAlt;

  estados: Estado[] = [];
  municipios: Municipio[] = [];
  calles: Calle[] = [];
  colonias: Colonia[] = [];
  localidades: Localidad[] = [];

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

  obtenerCallesTimeout = undefined;

  fechaDeHoy = new Date().toISOString().split('T')[0];
  modal: NgbModalRef;
  closeResult: string;
  cliente: Cliente;
  showDomicilioForm: boolean = false;

  stepper: Stepper;

  private gridApi;
  private gridColumnApi;

  columnDefs = Cliente.obtenerColumnasPorDefault();
  allColumnDefs = Cliente.obtenerTodasLasColumnas();
  rowData = [];

  frameworkComponents: any;
  rowDataClicked = {
    uuid: undefined
  };

  nuevoClienteForm: FormGroup;
  modificarClienteForm: FormGroup;
  nuevoClienteDomicilioForm: FormGroup;

  domicilios: ClienteDomicilio[] = [];
  domicilio: ClienteDomicilio;

  tiposInfraestructura: TipoInfraestructura[] = [];
  tipoInfraestructura: TipoInfraestructura = undefined;

  pestanaActual: string = "DETALLES";
  validacionRFCResponse = undefined;

  tempFile;
  pdfActual;

  estadoSearchForm: FormGroup;
  municipioSearchForm: FormGroup;
  localidadSearchForm: FormGroup;
  calleSearchForm: FormGroup;
  coloniaSearchForm: FormGroup;
  motivosEliminacionForm: FormGroup;

  tempUuidDomicilioCliente: string = "";
  tempUuidCliente: string = "";

  editandoDomicilio: boolean = false;

  clienteGuardado: boolean = false;
  domiciliosGuardados: boolean = false;

  temporaryIndex: number;

  @ViewChild('eliminarClienteModal') eliminarClienteModal;
  @ViewChild('eliminarDomicilioClienteModal') eliminarDomicilioClienteModal;
  @ViewChild('modificarClienteModal') modificarClienteModal;
  @ViewChild('quitarDomicilioClienteModal') quitarDomicilioClienteModal;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaClienteService: EmpresaClientesService,
              private formBuilder: FormBuilder, private tipoInfraestructuraService: TipoInfraestructuraService,
              private estadoService: EstadosService, private calleService: CalleService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.nuevoClienteForm = this.formBuilder.group({
      tipoPersona: ['', Validators.required],
      rfc: ['', [Validators.required, Validators.minLength(12), Validators.maxLength(13)]],
      nombreComercial: ['', [Validators.required, Validators.maxLength(100)]],
      razonSocial: ['', [Validators.required, Validators.maxLength(100)]],
      canes: ['', Validators.required],
      armas: ['', Validators.required],
      fechaInicio: ['', Validators.required],
      archivo: ['']
    });

    this.modificarClienteForm = this.formBuilder.group({
      tipoPersona: ['', Validators.required],
      rfc: ['', [Validators.required, Validators.minLength(12), Validators.maxLength(13)]],
      nombreComercial: ['', [Validators.required, Validators.maxLength(100)]],
      razonSocial: ['', [Validators.required, Validators.maxLength(100)]],
      canes: ['', Validators.required],
      armas: ['', Validators.required],
      fechaInicio: ['', Validators.required]
    });

    this.nuevoClienteDomicilioForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      matriz: ['', Validators.required],
      numeroExterior: ['', [Validators.required, Validators.maxLength(20)]],
      numeroInterior: ['', [Validators.maxLength(20)]],
      domicilio4: ['', Validators.maxLength(100)],
      codigoPostal: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(5)]],
      pais: ['Mexico', [Validators.required, Validators.maxLength(100)]],
      telefonoFijo: ['', [Validators.required]],
      telefonoMovil: ['', [Validators.required]],
      contacto: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoPaternoContacto: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaternoContacto: ['', [Validators.required, Validators.maxLength(60)]],
      correoElectronico: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
      tipoInfraestructura: ['', Validators.required],
      tipoInfraestructuraOtro: ['', Validators.maxLength(30)]
    })

    this.estadoSearchForm = this.formBuilder.group({
      nombre: ['']
    });

    this.municipioSearchForm = this.formBuilder.group({
      nombre: ['']
    });

    this.localidadSearchForm = this.formBuilder.group({
      nombre: ['']
    })

    this.coloniaSearchForm = this.formBuilder.group({
      nombre: ['']
    });

    this.calleSearchForm = this.formBuilder.group({
      nombre: ['']
    })

    this.motivosEliminacionForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: ['', Validators.required],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.empresaClienteService.obtenerClientes().subscribe((data: Cliente[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar los clientes. ${error}`,
        ToastType.ERROR
      )
    });

    this.tipoInfraestructuraService.obtenerTiposInfraestructura().subscribe((data: TipoInfraestructura[]) => {
      this.tiposInfraestructura = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los tipos de infraestructura. Motivo: ${error}`,
        ToastType.ERROR
      )
    });

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

  modificarDomicilio(i) {
    this.domicilio = this.domicilios[i];
    this.domicilios.splice(i, 1);
    this.nuevoClienteDomicilioForm.patchValue({
      nombre: this.domicilio.nombre,
      matriz: this.domicilio.matriz,
      numeroExterior: this.domicilio.numeroExterior,
      numeroInterior: this.domicilio.numeroInterior,
      domicilio4: this.domicilio.domicilio4,
      codigoPostal: this.domicilio.codigoPostal,
      pais: this.domicilio.pais,
      telefonoFijo: this.domicilio.telefonoFijo,
      telefonoMovil: this.domicilio.telefonoMovil,
      contacto: this.domicilio.contacto,
      apellidoPaternoContacto: this.domicilio.apellidoPaternoContacto,
      apellidoMaternoContacto: this.domicilio.apellidoMaternoContacto,
      correoElectronico: this.domicilio.correoElectronico,
      tipoInfraestructura: this.domicilio.tipoInfraestructura.uuid,
      tipoInfraestructuraOtro: this.domicilio.tipoInfraestructuraOtro
    });

    this.calle = this.domicilio.calleCatalogo;
    this.colonia = this.domicilio.coloniaCatalogo;
    this.localidad = this.domicilio.localidadCatalogo;
    this.municipio = this.domicilio.municipioCatalogo;
    this.estado = this.domicilio.estadoCatalogo;
  }

  mostrarQuitarClienteDomicilioModal(i) {
    this.temporaryIndex = i;
    this.modal = this.modalService.open(this.quitarDomicilioClienteModal, {size: "lg"});
  }

  confirmarQuitarDomicilioCliente() {
    this.domicilios.splice(this.temporaryIndex, 1);
    this.modal.close();
  }

  seleccionarEstado(estadoUuid) {
    this.estado = this.estados.filter(x => x.uuid === estadoUuid)[0];
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

    this.nuevoClienteDomicilioForm.patchValue({
      estado: undefined,
      domicilio3: undefined,
      domicilio2: undefined
    })
  }

  seleccionarMunicipio(municipioUuid) {
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
    this.nuevoClienteDomicilioForm.patchValue({
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

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  cambiarPestana(status) {
    if(status == this.pestanaActual) {
      return;
    }
    this.pestanaActual = status;
  }

  validarRfc(event) {
    this.validacionRFCResponse = validateRfc(event.value);
    if(!this.validacionRFCResponse.isValid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El RFC ingresado no es valido. Motivo: ${this.validacionRFCResponse.errors}`,
        ToastType.WARNING
      );
      return;
    }
  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
  }

  mostrarModalCrear(crearDomicilioModal) {
    this.modal = this.modalService.open(crearDomicilioModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

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

  seleccionarTipoInfraestructura(target) {
    let uuid = target.value;
    this.tipoInfraestructura = this.tiposInfraestructura.filter(x => x.uuid === uuid)[0];
  }

  finalizar() {
    window.location.reload();
  }

  next(stepName: string, form) {
    if(form !== undefined && !form.valid && !this.clienteGuardado) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenarse",
        ToastType.WARNING
      );
      return;
    }

    switch (stepName) {
      case "DOMICILIOS":
        if(this.clienteGuardado) {
          this.stepper.next();
          return;
        }

        let formValue: Cliente = form.value;
        let formData = new FormData();

        formData.append('cliente', JSON.stringify(formValue));

        if(this.tempFile !== undefined) {
          formData.append('archivo', this.tempFile, this.tempFile.name);
        } else {
          formData.append('archivo', null)
        }

        this.empresaClienteService.guardarCliente(formData).subscribe((data: Cliente) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se ha guardado el cliente con exito",
            ToastType.SUCCESS
          );
          this.clienteGuardado = true;
          this.desactivarCamposCliente();
          this.cliente = data;
          this.stepper.next();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido guardar el cliente. ${error}`,
            ToastType.ERROR
          )
        });
        break;
      case "RESUMEN":
        if(this.domiciliosGuardados) {
          this.stepper.next();
          return;
        }

        if(this.domicilios.length < 1) {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            "No hay domicilios creados aun para la empresa.",
            ToastType.WARNING
          );
          return;
        }

        this.toastService.showGenericToast(
          "Espere un momento",
          "Estamos guardando los domicilios",
          ToastType.INFO
        );

        this.empresaClienteService.guardarDomicilioCliente(this.cliente.uuid, this.domicilios).subscribe((data) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se han guardado los domicilios con exito",
            ToastType.SUCCESS
          );
          this.domiciliosGuardados = true;
          this.desactivarCamposDireccion();
          this.stepper.next();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los domicilios del cliente. Motivo: ${error}`,
            ToastType.ERROR
          );
        });

        break;
    }
  }

  previous() {
    this.stepper.previous();
  }

  mostrarModalDetalles(rowData, modal) {

    let clienteUuid = rowData.uuid;

    this.empresaClienteService.obtenerClientePorUuid(clienteUuid).subscribe((data: Cliente) => {
      this.cliente = data;
      this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});
      this.modal.result.then((result) => {
        this.closeResult = `Closed with ${result}`;
      }, (error) => {
        this.closeResult = `Dismissed ${this.getDismissReason(error)}`
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion del cliente. ${error}`,
        ToastType.ERROR
      )
    })
  }


  mostrarNuevoDomicilioForm() {
    this.showDomicilioForm = !this.showDomicilioForm;
    if(!this.showDomicilioForm) {
      this.nuevoClienteDomicilioForm.reset();
      this.estado = undefined;
      this.municipio = undefined;
      this.localidad = undefined;
      this.colonia = undefined;
      this.calle = undefined;
    }
  }

  guardarDomicilio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenar",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando el domicilio del cliente",
      ToastType.INFO
    );

    let domicilio: ClienteDomicilio = form.value;
    let tipoInfraestructura: TipoInfraestructura = this.tiposInfraestructura.filter(x => x.uuid === form.value.tipoInfraestructura)[0];

    if(tipoInfraestructura !== undefined && tipoInfraestructura.nombre === "Otro" && domicilio.tipoInfraestructuraOtro === "") {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Cuando el tipo de infraestructura es otro, se debe especificar el tipo. Favor de especificarlo",
        ToastType.WARNING
      );
      return;
    }

    domicilio.tipoInfraestructura = tipoInfraestructura;
    domicilio.estadoCatalogo = this.estado;
    domicilio.municipioCatalogo = this.municipio;
    domicilio.coloniaCatalogo = this.colonia;
    domicilio.calleCatalogo = this.calle;
    domicilio.localidadCatalogo = this.localidad;

    if(this.editandoDomicilio) {
      domicilio.id = this.domicilio.id;
      domicilio.uuid = this.domicilio.uuid;

      this.empresaClienteService.modificarDomicilioCliente(this.cliente.uuid, this.domicilio.uuid, domicilio).subscribe((data) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se guardo el domicilio con exito",
          ToastType.SUCCESS
        );
        this.mostrarNuevoDomicilioForm();
        this.empresaClienteService.obtenerClienteDomicilios(this.cliente.uuid).subscribe((data: ClienteDomicilio[]) => {
          this.cliente.domicilios = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los domicilios del cliente. Motivo: ${error}`,
            ToastType.ERROR
          )
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el domicilio del cliente. Motivo: ${error}`,
          ToastType.ERROR
        )
      });
    } else {
      let tempArray: ClienteDomicilio[] = [domicilio];

      this.empresaClienteService.guardarDomicilioCliente(this.cliente.uuid, tempArray).subscribe((data: ClienteDomicilio) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha guardado el domicilio del cliente con exito",
          ToastType.SUCCESS
        );
        this.mostrarNuevoDomicilioForm();
        this.empresaClienteService.obtenerClienteDomicilios(this.cliente.uuid).subscribe((data: ClienteDomicilio[]) => {
          this.cliente.domicilios = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los domicilios del cliente. Motivo: ${error}`,
            ToastType.ERROR
          )
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el domicilio. Motivo: ${error}`,
          ToastType.ERROR
        )
      });
    }
  }

  agregarDomicilio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenar",
        ToastType.WARNING
      );
      return;
    }

    let formData: ClienteDomicilio = form.value;
    formData.tipoInfraestructura = this.tipoInfraestructura;
    if(formData.tipoInfraestructura.nombre === "Otro" && formData.tipoInfraestructuraOtro === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El tipo de infraestructura es otro y no se especifico el tipo",
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

    let domicilio: ClienteDomicilio = form.value;
    domicilio.estadoCatalogo = this.estado;
    domicilio.municipioCatalogo = this.municipio;
    domicilio.localidadCatalogo = this.localidad;
    domicilio.coloniaCatalogo = this.colonia;
    domicilio.calleCatalogo = this.calle;

    let tipoInfraestructura: TipoInfraestructura = this.tiposInfraestructura.filter(x => x.uuid === form.value.tipoInfraestructura)[0];

    if(tipoInfraestructura !== undefined && tipoInfraestructura.nombre === "Otro" && domicilio.tipoInfraestructuraOtro === "") {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Cuando el tipo de infraestructura es otro, se debe especificar el tipo. Favor de especificarlo",
        ToastType.WARNING
      );
      return;
    }

    this.domicilios.push(formData);
    this.nuevoClienteDomicilioForm.reset();
    this.nuevoClienteDomicilioForm.patchValue({
      pais: 'Mexico'
    })

    this.estado = undefined;
    this.municipio = undefined;
    this.localidad = undefined;
    this.colonia = undefined;
    this.calle = undefined;
  }

  mostrarModalEditarCliente() {
    this.modal = this.modalService.open(this.modificarClienteModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl', backdrop: "static"})

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })

    this.modificarClienteForm.patchValue({
      tipoPersona: this.cliente.tipoPersona,
      rfc: this.cliente.rfc,
      nombreComercial: this.cliente.nombreComercial,
      razonSocial: this.cliente.razonSocial,
      canes: this.cliente.canes,
      armas: this.cliente.armas,
      fechaInicio: this.cliente.fechaInicio
    })
  }

  mostrarEditarDomicilioCliente(index) {
    this.domicilio = this.cliente.domicilios[index];
    this.mostrarNuevoDomicilioForm();
    this.editandoDomicilio = true;
    this.nuevoClienteDomicilioForm.patchValue({
      nombre: this.domicilio.nombre,
      matriz: this.domicilio.matriz,
      numeroExterior: this.domicilio.numeroExterior,
      numeroInterior: this.domicilio.numeroInterior,
      domicilio4: this.domicilio.domicilio4,
      codigoPostal: this.domicilio.codigoPostal,
      pais: this.domicilio.pais,
      telefonoFijo: this.domicilio.telefonoFijo,
      telefonoMovil: this.domicilio.telefonoMovil,
      contacto: this.domicilio.contacto,
      apellidoPaternoContacto: this.domicilio.apellidoPaternoContacto,
      apellidoMaternoContacto: this.domicilio.apellidoMaternoContacto,
      correoElectronico: this.domicilio.correoElectronico,
      tipoInfraestructura: this.domicilio.tipoInfraestructura.uuid,
      tipoInfraestructuraOtro: this.domicilio.tipoInfraestructuraOtro
    });

    this.calle = this.domicilio.calleCatalogo;
    this.colonia = this.domicilio.coloniaCatalogo;
    this.localidad = this.domicilio.localidadCatalogo;
    this.municipio = this.domicilio.municipioCatalogo;
    this.estado = this.domicilio.estadoCatalogo;
  }

  mostrarModalEliminarCliente(uuid) {
    this.tempUuidDomicilioCliente = uuid;
    this.modal = this.modalService.open(this.eliminarClienteModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarDomicilioCliente(uuid) {
    this.tempUuidDomicilioCliente = uuid;
    this.modal = this.modalService.open(this.eliminarDomicilioClienteModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  guardarCambiosCliente(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario esta invalido. Favor de verificarlo",
        ToastType.WARNING
      );
      return;
    }

    let formValue: Cliente = form.value;

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando el cliente",
      ToastType.INFO
    );

    this.empresaClienteService.modificarCliente(this.cliente.uuid, formValue).subscribe((data: Cliente) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha modificado el cliente con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar el cliente. ${error}`,
        ToastType.ERROR
      )
    });
  }

  confirmarEliminarCliente(form) {
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
      "Se esta eliminando el cliente",
      ToastType.INFO
    );

    let formValue: Cliente = form.value;

    let formData = new FormData();
    formData.append('cliente', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.empresaClienteService.eliminarCliente(this.cliente.uuid, formData).subscribe((data: Cliente) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el cliente con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El cliente no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarDomicilioCliente() {
    if(this.tempUuidDomicilioCliente === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del domicilio del cliente a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el domicilio del cliente",
      ToastType.INFO
    );

    this.empresaClienteService.eliminarDomicilioCliente(this.cliente.uuid, this.tempUuidDomicilioCliente).subscribe((data: ClienteDomicilio) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el domicilio con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.empresaClienteService.obtenerClienteDomicilios(this.cliente.uuid).subscribe((data: ClienteDomicilio[]) => {
        this.cliente.domicilios = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar los domicilios del cliente. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El domicilio del cliente no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  private desactivarCamposCliente() {
    this.nuevoClienteForm.controls['tipoPersona'].disable();
    this.nuevoClienteForm.controls['rfc'].disable();
    this.nuevoClienteForm.controls['nombreComercial'].disable();
    this.nuevoClienteForm.controls['razonSocial'].disable();
    this.nuevoClienteForm.controls['canes'].disable();
    this.nuevoClienteForm.controls['armas'].disable();
    this.nuevoClienteForm.controls['fechaInicio'].disable();
    this.nuevoClienteForm.controls['archivo'].disable();
  }

  private desactivarCamposDireccion() {
    this.nuevoClienteDomicilioForm.controls['nombre'].disable();
    this.nuevoClienteDomicilioForm.controls['numeroExterior'].disable();
    this.nuevoClienteDomicilioForm.controls['numeroInterior'].disable();
    this.nuevoClienteDomicilioForm.controls['domicilio4'].disable();
    this.nuevoClienteDomicilioForm.controls['codigoPostal'].disable();
    this.nuevoClienteDomicilioForm.controls['pais'].disable();
    this.nuevoClienteDomicilioForm.controls['matriz'].disable();
    this.nuevoClienteDomicilioForm.controls['telefonoFijo'].disable();
    this.nuevoClienteDomicilioForm.controls['telefonoMovil'].disable();
    this.nuevoClienteDomicilioForm.controls['contacto'].disable();
    this.nuevoClienteDomicilioForm.controls['apellidoPaternoContacto'].disable();
    this.nuevoClienteDomicilioForm.controls['apellidoMaternoContacto'].disable();
    this.nuevoClienteDomicilioForm.controls['correoElectronico'].disable();
    this.nuevoClienteDomicilioForm.controls['tipoInfraestructura'].disable();
    this.nuevoClienteDomicilioForm.controls['tipoInfraestructuraOtro'].disable();
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
