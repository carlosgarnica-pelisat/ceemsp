import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import EmpresaEscritura from "../../_models/EmpresaEscritura";
import {ToastService} from "../../_services/toast.service";
import {EmpresaService} from "../../_services/empresa.service";
import {ToastType} from "../../_enums/ToastType";
import EmpresaEscrituraSocio from "../../_models/EmpresaEscrituraSocio";
import {faCheck, faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import EmpresaEscrituraApoderado from "../../_models/EmpresaEscrituraApoderado";
import EmpresaEscrituraRepresentante from "../../_models/EmpresaEscrituraRepresentante";
import EmpresaEscrituraConsejo from "../../_models/EmpresaEscrituraConsejo";
import ExisteEscritura from "../../_models/ExisteEscritura";
import Estado from "../../_models/Estado";
import Municipio from "../../_models/Municipio";
import Localidad from "../../_models/Localidad";
import {EstadosService} from "../../_services/estados.service";
import curp from 'curp';
import {LegalService} from "../../_services/legal.service";
import {ValidacionService} from "../../_services/validacion.service";
import {Table} from "primeng/table";
import {formatDate} from "@angular/common";
import {
  BotonEmpresaDomiciliosComponent
} from "../../_components/botones/boton-empresa-domicilios/boton-empresa-domicilios.component";
import {BotonEmpresaLegalComponent} from "../../_components/botones/boton-empresa-legal/boton-empresa-legal.component";

@Component({
  selector: 'app-empresa-legal',
  templateUrl: './empresa-legal.component.html',
  styleUrls: ['./empresa-legal.component.css']
})
export class EmpresaLegalComponent implements OnInit {

  fechaDeHoy = new Date().toISOString().split('T')[0];

  showSocioForm: boolean = false;
  showApoderadoForm: boolean = false;
  showRepresentanteForm: boolean = false;
  showConsejoForm: boolean = false;

  editandoSocio: boolean = false;
  editandoApoderado: boolean = false;
  editandoRepresentante: boolean = false;
  editandoConsejo: boolean = false;

  uuid: string;
  pestanaActual: string = "DETALLES";
  escrituras: EmpresaEscritura[];

  socio: EmpresaEscrituraSocio;
  apoderado: EmpresaEscrituraApoderado;
  representante: EmpresaEscrituraRepresentante;
  consejo: EmpresaEscrituraConsejo;

  nuevaEscrituraForm: FormGroup;
  nuevoSocioForm: FormGroup;
  nuevoApoderadoForm: FormGroup;
  nuevoRepresentanteForm: FormGroup;
  nuevoConsejoAdministracionForm: FormGroup;
  estadoSearchForm: FormGroup;
  municipioSearchForm: FormGroup;
  localidadSearchForm: FormGroup;

  motivosEliminacionSocioForm: FormGroup;
  motivosEliminacionAopderadoForm: FormGroup;
  motivosEliminacionRepresentanteForm: FormGroup;
  motivosEliminacionConsejoForm: FormGroup;

  modal: NgbModalRef;
  closeResult: string;
  escritura: EmpresaEscritura;
  existeEscritura: ExisteEscritura;

  faPencil = faPencilAlt;
  faTrash = faTrash;
  faCheck = faCheck;

  private gridApi;
  private gridColumnApi;

  columnDefs = [
    {headerName: 'No. Instrumento', field: 'numeroEscritura', sortable: true, filter: true },
    {headerName: 'Fecha', field: 'fechaEscritura', sortable: true, filter: true },
    {headerName: 'Ciudad', sortable: true, filter: true, valueGetter: function (params) {return params.data.localidadCatalogo.nombre + ", " + params.data.estadoCatalogo.nombre}},
    {headerName: 'Nombre y Numero del fedatario', sortable: true, filter: true, valueGetter: function(params) {return `${params.data.numero} - ${params.data.nombreFedatario} ${params.data.apellidoPaterno} ${params.data.apellidoMaterno}`}},
    {headerName: 'Ciudad', field: 'ciudad', sortable: true, filter: true},
    {headerName: 'Opciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
        label: 'Ver detalles',
        verDetalles: this.verDetalles.bind(this)
      }}
  ];
  allColumnDefs = EmpresaEscritura.obtenerTodasLasColumnas();
  rowData = [];

  tempFile;
  pdfActual;

  frameworkComponents: any;
  rowDataClicked = {
    uuid: undefined
  };

  estados: Estado[] = [];
  municipios: Municipio[] = [];
  localidades: Localidad[] = [];

  estado: Estado;
  municipio: Municipio;
  localidad: Localidad;

  porcentaje: number = 0.00;

  tempUuidSocio: string;
  tempUuidApoderado: string;
  tempUuidRepresentante: string;
  tempUuidConsejo: string;

  estadoQuery: string = '';
  municipioQuery: string = '';
  localidadQuery: string = '';

  @ViewChild('mostrarDetallesEscrituraModal') mostrarDetallesEscrituraModal: any;

  @ViewChild('eliminarEscrituraSocioModal') eliminarEscrituraSocioModal;
  @ViewChild('eliminarEscrituraApoderadoModal') eliminarEscrituraApoderadoModal;
  @ViewChild('eliminarEscrituraRepresentanteModal') eliminarEscrituraRepresentanteModal;
  @ViewChild('eliminarEscrituraConsejoModal') eliminarEscrituraConsejoModal;

  @ViewChild('agregarSocioModal') agregarSocioModal;
  @ViewChild('agregarApoderadoModal') agregarApoderadoModal;
  @ViewChild('agregarRepresentanteModal') agregarRepresentanteModal;
  @ViewChild('agregarConsejoModal') agregarConsejoModal;

  @ViewChild('mostrarMotivosEliminacionSocio') mostrarMotivosEliminacionSocio;
  @ViewChild('mostrarMotivosEliminacionApoderado') mostrarMotivosEliminacionApoderado;
  @ViewChild('mostrarMotivosEliminacionRepresentante') mostrarMotivosEliminacionRepresentante;
  @ViewChild('mostrarMotivosEliminacionConsejo') mostrarMotivosEliminacionConsejo;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private legalService: LegalService,
              private formBuilder: FormBuilder, private estadosService: EstadosService,
              private validacionService: ValidacionService) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      buttonRenderer: BotonEmpresaLegalComponent
    }

    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.nuevaEscrituraForm = this.formBuilder.group({
      numeroEscritura: ['', [Validators.required, Validators.maxLength(10)]],
      fechaEscritura: ['', Validators.required],
      ciudad: ['', [Validators.required, Validators.maxLength(60)]],
      tipoFedatario: ['', Validators.required],
      numero: ['', [Validators.required, Validators.min(1), Validators.max(9999)]],
      nombreFedatario: ['', [Validators.required, Validators.maxLength(100)]],
      apellidoPaterno: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.required, Validators.maxLength(60)]],
      curp: ['', [Validators.required, Validators.minLength(18), Validators.maxLength(18)]],
      descripcion: ['', Validators.required]
    })

    this.nuevoSocioForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      porcentajeAcciones: ['', Validators.required],
      curp: ['', [Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.nuevoApoderadoForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      fechaInicio: [''],
      fechaFin: [''],
      curp: ['', [Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.nuevoRepresentanteForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      curp: ['', [Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.nuevoConsejoAdministracionForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      puesto: ['', Validators.required],
      curp: ['', [Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.legalService.obtenerEscrituras().subscribe((data: EmpresaEscritura[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las escrituras. ${error}`,
        ToastType.ERROR
      )
    })

    this.motivosEliminacionSocioForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: [''],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.motivosEliminacionAopderadoForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: [''],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.motivosEliminacionRepresentanteForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: [''],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.motivosEliminacionConsejoForm = this.formBuilder.group({
      motivoBaja: ['', [Validators.required, Validators.maxLength(60)]],
      observacionesBaja: [''],
      fechaBaja: ['', Validators.required],
      documentoFundatorioBaja: ['']
    });

    this.estadosService.obtenerEstados().subscribe((data: Estado[]) => {
      this.estados = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los estados. Motivo: ${error}`,
        ToastType.ERROR
      );
    });
  }

  verDetalles(rowData) {
    this.mostrarModalDetalles(rowData.rowData, this.mostrarDetallesEscrituraModal)
  }

  seleccionarEstado(estadoUuid) {
    this.estado = this.estados.filter(x => x.uuid === estadoUuid)[0];
    this.estadosService.obtenerEstadosPorMunicipio(estadoUuid).subscribe((data: Municipio[]) => {
      this.municipios = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar los municipios. Motivo: ${error}`,
        ToastType.ERROR
      )
    });
  }

  seleccionarMunicipio(municipioUuid) {
    this.municipio = this.municipios.filter(x => x.uuid === municipioUuid)[0];

    this.estadosService.obtenerLocalidadesPorMunicipioYEstado(this.estado.uuid, municipioUuid).subscribe((data: Localidad[]) => {
      this.localidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las localidades. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  seleccionarLocalidad(localidadUuid) {
    this.localidad = this.localidades.filter(x => x.uuid === localidadUuid)[0];

    this.nuevaEscrituraForm.patchValue({
      ciudad: this.localidad.nombre
    })
  }

  validarEscritura(event) {
    let numeroEscritora = event.value;
    let existeEscritura = new ExisteEscritura();
    existeEscritura.numero = numeroEscritora

    this.validacionService.validarEscritura(existeEscritura).subscribe((data: ExisteEscritura) => {
      this.existeEscritura = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido validar la escritura. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  eliminarEstado() {
    this.estado = undefined;
    this.municipio = undefined;
    this.localidad = undefined;
  }

  eliminarMunicipio() {
    this.municipio = undefined;
    this.localidad = undefined;
  }

  eliminarLocalidad() {
    this.localidad = undefined;
  }

  mostrarFormularioNuevoSocio() {
    this.showSocioForm = !this.showSocioForm;
    if(!this.showSocioForm) {
      this.nuevoSocioForm.reset();
    }
    if(this.editandoSocio) {
      this.editandoSocio = false;
      this.socio = undefined;
    }
  }

  mostrarFormularioNuevoApoderado() {
    this.showApoderadoForm = !this.showApoderadoForm;
    if(!this.showSocioForm) {
      this.nuevoApoderadoForm.reset();
    }
    if(this.editandoApoderado) {
      this.editandoApoderado = false;
    }
  }

  mostrarFormularioNuevoRepresentante() {
    this.showRepresentanteForm = !this.showRepresentanteForm;
    if(!this.showRepresentanteForm) {
      this.nuevoRepresentanteForm.reset();
    }
    if(this.editandoRepresentante) {
      this.editandoRepresentante = false;
    }
  }

  mostrarFormularioNuevoConsejo() {
    this.showConsejoForm = !this.showConsejoForm;
    if(!this.showConsejoForm) {
      this.nuevoConsejoAdministracionForm.reset();
    }
    if(this.editandoConsejo) {
      this.editandoConsejo = false;
    }
  }

  mostrarEditarSocio(uuid) {
    this.socio = this.escritura.socios.filter(x => x.uuid === uuid)[0];
    this.mostrarFormularioNuevoSocio();
    this.modal = this.modalService.open(this.agregarSocioModal, {size: 'xl', backdrop: 'static'})
    this.editandoSocio = true;
    this.nuevoSocioForm.patchValue({
      nombres: this.socio.nombres,
      apellidos: this.socio.apellidos,
      apellidoMaterno: this.socio.apellidoMaterno,
      curp: this.socio.curp,
      sexo: this.socio.sexo,
      porcentajeAcciones: this.socio.porcentajeAcciones
    });
  }

  mostrarEditarApoderado(uuid) {
    this.apoderado = this.escritura.apoderados.filter(x => x.uuid === uuid)[0];
    this.mostrarFormularioNuevoApoderado();
    this.modal = this.modalService.open(this.agregarApoderadoModal, {size: 'xl', backdrop: 'static'})
    this.editandoApoderado = true;
    this.nuevoApoderadoForm.patchValue({
      nombres: this.apoderado.nombres,
      apellidos: this.apoderado.apellidos,
      apellidoMaterno: this.apoderado.apellidoMaterno,
      curp: this.apoderado.curp,
      sexo: this.apoderado.sexo,
      fechaInicio: this.apoderado.fechaInicio,
      fechaFin: this.apoderado.fechaFin
    });
  }

  mostrarEditarRepresentante(uuid) {
    this.representante = this.escritura.representantes.filter(x => x.uuid === uuid)[0];
    this.mostrarFormularioNuevoRepresentante();
    this.modal = this.modalService.open(this.agregarRepresentanteModal, {size: 'xl', backdrop: 'static'})
    this.editandoRepresentante = true;
    this.nuevoRepresentanteForm.patchValue({
      nombres: this.representante.nombres,
      apellidos: this.representante.apellidos,
      apellidoMaterno: this.representante.apellidoMaterno,
      curp: this.representante.curp,
      sexo: this.representante.sexo
    });
  }

  mostrarEditarConsejo(uuid) {
    this.consejo = this.escritura.consejos.filter(x => x.uuid === uuid)[0];
    this.mostrarFormularioNuevoConsejo();
    this.modal = this.modalService.open(this.agregarConsejoModal, {size: 'xl', backdrop: 'static'})
    this.editandoConsejo = true;
    this.nuevoConsejoAdministracionForm.patchValue({
      nombres: this.consejo.nombres,
      apellidos: this.consejo.apellidos,
      apellidoMaterno: this.consejo.apellidoMaterno,
      curp: this.consejo.curp,
      sexo: this.consejo.sexo,
      puesto: this.consejo.puesto
    })
  }

  mostrarModalEliminarSocio(uuid) {
    this.tempUuidSocio = uuid;
    this.motivosEliminacionSocioForm.patchValue({
      fechaBaja: formatDate(new Date(), "yyyy-MM-dd", "en")
    });
    this.motivosEliminacionSocioForm.controls['fechaBaja'].disable();

    this.modal = this.modalService.open(this.eliminarEscrituraSocioModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarApoderado(uuid) {
    this.tempUuidApoderado = uuid;
    this.motivosEliminacionAopderadoForm.patchValue({
      fechaBaja: formatDate(new Date(), "yyyy-MM-dd", "en")
    });
    this.motivosEliminacionAopderadoForm.controls['fechaBaja'].disable();
    this.modal = this.modalService.open(this.eliminarEscrituraApoderadoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarRepresentante(uuid) {
    this.tempUuidRepresentante = uuid;
    this.motivosEliminacionRepresentanteForm.patchValue({
      fechaBaja: formatDate(new Date(), "yyyy-MM-dd", "en")
    });
    this.motivosEliminacionRepresentanteForm.controls['fechaBaja'].disable();
    this.modal = this.modalService.open(this.eliminarEscrituraRepresentanteModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarConsejo(uuid) {
    this.tempUuidConsejo = uuid;
    this.motivosEliminacionConsejoForm.patchValue({
      fechaBaja: formatDate(new Date(), "yyyy-MM-dd", "en")
    });
    this.motivosEliminacionConsejoForm.controls['fechaBaja'].disable();
    this.modal = this.modalService.open(this.eliminarEscrituraConsejoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  guardarSocio(nuevoSocioform) {

    if(!nuevoSocioform.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan campos requeridos por rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando el socio",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraSocio = nuevoSocioform.value;

    if(formValue.curp !== null && formValue.curp !== "") {
      if(!curp.validar(formValue.curp)) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La CURP ingresada para el socio no es valida",
          ToastType.WARNING
        );
        return;
      }

      let existeSocioRfc = this.escritura.socios.filter(x => (x.curp === formValue.curp && x.uuid !== this.socio?.uuid))
      if(existeSocioRfc.length > 0) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Ya hay un socio registrado con este CURP",
          ToastType.WARNING
        );
        return;
      }
    }

    if(this.escritura.socios.length > 0) {
      this.porcentaje = 0.00;
      this.escritura.socios.forEach((s) => {
        if(this.editandoSocio && s.uuid === this.socio.uuid) {
          return;
        }
        this.porcentaje += parseInt(String(s.porcentajeAcciones));
      })

      this.porcentaje += parseInt(String(formValue.porcentajeAcciones));

      if(this.porcentaje > 100) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `El porcentaje de acciones es mayor al 100%`,
          ToastType.WARNING
        );
        return;
      }
    } else {
      if(formValue.porcentajeAcciones > 100) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `El porcentaje de acciones es mayor al 100%`,
          ToastType.WARNING
        );
        return;
      }
    }

    if(this.editandoSocio) {
      formValue.uuid = this.socio.uuid;
      formValue.id = this.socio.id;

      this.legalService.modificarEscrituraSocio(this.escritura.uuid, this.socio.uuid, formValue).subscribe((data: EmpresaEscrituraSocio) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el socio con exito",
          ToastType.SUCCESS
        );
        this.modal.close();
        this.mostrarFormularioNuevoSocio();
        this.legalService.obtenerEscrituraSocios(this.escritura.uuid).subscribe((data: EmpresaEscrituraSocio[]) => {
          this.escritura.socios = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los socios. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el socio. ${error}`,
          ToastType.ERROR
        );
      });

    } else {
      this.legalService.guardarEscrituraSocio(this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraSocio) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha registrado el socio con exito",
          ToastType.SUCCESS
        );
        this.legalService.obtenerEscrituraSocios(this.escritura.uuid).subscribe((data: EmpresaEscrituraSocio[]) => {
          this.escritura.socios = data;
          this.modal.close();
          this.mostrarFormularioNuevoSocio();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se han podido descargar los socios. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el socio. ${error}`,
          ToastType.ERROR
        );
      });
    }
  }

  guardarApoderado(nuevoApoderadoForm) {

    if(!nuevoApoderadoForm.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan campos requeridos por rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    let formValue: EmpresaEscrituraApoderado = nuevoApoderadoForm.value;

    if(formValue.curp !== null && formValue.curp !== "") {
      if(!curp.validar(formValue.curp)) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La CURP ingresada para el socio no es valida",
          ToastType.WARNING
        );
        return;
      }

      let existeSocioRfc = this.escritura.apoderados.filter(x => (x.curp === formValue.curp && x.uuid !== this.apoderado?.uuid))
      if(existeSocioRfc.length > 0) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Ya hay un apoderado registrado con este CURP",
          ToastType.WARNING
        );
        return;
      }
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

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el nuevo apoderado",
      ToastType.INFO
    );

    if(this.editandoApoderado) {
      formValue.uuid = this.apoderado.uuid;
      formValue.id = this.apoderado.id;

      this.legalService.modificarEscrituraApoderado(this.escritura.uuid, this.apoderado.uuid, formValue).subscribe((data: EmpresaEscrituraApoderado) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el apoderado con exito",
          ToastType.SUCCESS
        );
        this.modal.close();
        this.mostrarFormularioNuevoApoderado();
        this.legalService.obtenerEscriturasApoderados(this.escritura.uuid).subscribe((data: EmpresaEscrituraApoderado[]) => {
          this.escritura.apoderados = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los apoderados. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el apoderado. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
    } else {
      this.legalService.guardarEscrituraApoderado(this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraApoderado) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha registrado el apoderado con exito",
          ToastType.SUCCESS
        );
        this.modal.close();
        this.mostrarFormularioNuevoApoderado();
        this.legalService.obtenerEscriturasApoderados(this.escritura.uuid).subscribe((data: EmpresaEscrituraApoderado[]) => {
          this.escritura.apoderados = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los apoderados. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el apoderado. ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  guardarConsejo(nuevoConsejoForm) {
    if(!nuevoConsejoForm.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan campos requeridos por rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    let formValue: EmpresaEscrituraConsejo = nuevoConsejoForm.value;

    if(formValue.curp !== null && formValue.curp !== "") {
      if(!curp.validar(formValue.curp)) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La CURP ingresada para el socio no es valida",
          ToastType.WARNING
        );
        return;
      }

      let existeSocioRfc = this.escritura.consejos.filter(x => (x.curp === formValue.curp && x.uuid !== this.consejo?.uuid))
      if(existeSocioRfc.length > 0) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Ya hay un miembro del consejo registrado con este CURP",
          ToastType.WARNING
        );
        return;
      }
    }

    let existeConsejoPuesto = this.escritura.consejos.filter(x => x.puesto === formValue.puesto && x.uuid !== this.consejo?.uuid)
    if(existeConsejoPuesto.length > 0) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Ya hay un miembro del consejo registrado con este puesto",
        ToastType.WARNING
      );
      return;
    }

    if(this.editandoConsejo) {
      formValue.uuid = this.consejo.uuid;
      formValue.id = this.consejo.id;

      this.legalService.modificarEscrituraConsejo(this.escritura.uuid, this.consejo.uuid, formValue).subscribe((data: EmpresaEscrituraConsejo) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el miembro del consejo con exito",
          ToastType.SUCCESS
        );
        this.mostrarFormularioNuevoConsejo();
        this.modal.close();
        this.legalService.obtenerEscrituraConsejos(this.escritura.uuid).subscribe((data: EmpresaEscrituraConsejo[]) => {
          this.escritura.consejos = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los consejos. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el miembro del consejo. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
    } else {
      this.legalService.guardarEscrituraConsejos(this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraConsejo) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha registrado el miembro del consejo con exito",
          ToastType.SUCCESS
        );
        this.modal.close();
        this.mostrarFormularioNuevoConsejo();
        this.legalService.obtenerEscrituraConsejos(this.escritura.uuid).subscribe((data: EmpresaEscrituraConsejo[]) => {
          this.escritura.consejos = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los consejos. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el miembro del consejo. ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  guardarRepresentante(nuevoRepresentanteForm) {

    if(!nuevoRepresentanteForm.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan campos requeridos por rellenar. Favor de rellenarlos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el nuevo representante",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraRepresentante = nuevoRepresentanteForm.value;

    if(formValue.curp !== null && formValue.curp !== "") {
      if(!curp.validar(formValue.curp)) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "La CURP ingresada para el socio no es valida",
          ToastType.WARNING
        );
        return;
      }

      let existeSocioRfc = this.escritura.representantes.filter(x => (x.curp === formValue.curp && x.uuid !== this.representante?.uuid))
      if(existeSocioRfc.length > 0) {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          "Ya hay un representante registrado con este CURP",
          ToastType.WARNING
        );
        return;
      }
    }

    if(this.editandoRepresentante) {
      formValue.uuid = this.representante.uuid;
      formValue.id = this.representante.id;

      this.legalService.modificarEscrituraRepresentante(this.escritura.uuid, this.representante.uuid, formValue).subscribe((data: EmpresaEscrituraRepresentante) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha actualizado el representante con exito",
          ToastType.SUCCESS
        );
        this.modal.close();
        this.mostrarFormularioNuevoRepresentante();
        this.legalService.obtenerEscrituraRepresentantes(this.escritura.uuid).subscribe((data: EmpresaEscrituraRepresentante[]) => {
          this.escritura.representantes = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los representantes. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido actualizar el representante. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.legalService.guardarEscrituraRepresentante(this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraRepresentante) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha registrado el representante con exito",
          ToastType.SUCCESS
        );
        this.modal.close();
        this.mostrarFormularioNuevoRepresentante();
        this.legalService.obtenerEscrituraRepresentantes(this.escritura.uuid).subscribe((data: EmpresaEscrituraRepresentante[]) => {
          this.escritura.representantes = data;
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido descargar los representantes. Motivo: ${error}`,
            ToastType.ERROR
          );
        })
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido guardar el representante. ${error}`,
          ToastType.ERROR
        );
      })
    }
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  onFileChange(event) {
    this.tempFile = event.target.files[0]
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

  mostrarModalDetalles(rowData, modal) {
    let escrituraUuid = rowData.uuid;
    this.legalService.obtenerEscrituraPorUuid(escrituraUuid).subscribe((data: EmpresaEscritura) => {
      this.escritura = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la escritura. ${error}`,
        ToastType.ERROR
      )
    })

    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  isColumnListed(field: string ) {
    return this.columnDefs.filter(s => s.field === field)[0] !== undefined;
  }

  cambiarPestana(status) {
    if(status == this.pestanaActual) {
      return;
    }
    this.pestanaActual = status;
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

  convertirPdf(pdf: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.pdfActual = reader.result;
    });

    if(pdf) {
      reader.readAsDataURL(pdf);
    }
  }

  mostrarEscritura(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'})

    this.legalService.descargarEscrituraPdf(this.escritura.uuid).subscribe((data: Blob) => {
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

  mostrarModalAgregarSocio() {
    this.modal = this.modalService.open(this.agregarSocioModal, {size: 'xl', backdrop: 'static'})
  }

  mostrarModalAgregarApoderado() {
    this.modal = this.modalService.open(this.agregarApoderadoModal, {size: 'xl', backdrop: 'static'})
  }

  mostrarModalAgregarRepresentante() {
    this.modal = this.modalService.open(this.agregarRepresentanteModal, {size: 'xl', backdrop: 'static'})
  }

  mostrarModalAgregarConsejo() {
    this.modal = this.modalService.open(this.agregarConsejoModal, {size: 'xl', backdrop: 'static'})
  }
  confirmarEliminarSocio(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Alguno de los parametros requeridos no se ha rellenado aun`,
        ToastType.WARNING
      );
      return;
    }

    if(this.tempUuidSocio === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del socio a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el socio",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraSocio = form.value;
    formValue.fechaBaja = formatDate(new Date(), "yyyy-MM-dd", "en")

    let formData = new FormData();
    formData.append('socio', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.legalService.eliminarEscrituraSocio(this.escritura.uuid, this.tempUuidSocio, formData).subscribe((data: EmpresaEscrituraSocio) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el socio con exito",
        ToastType.SUCCESS
      );
      this.tempFile = undefined;
      this.modal.close();

      this.legalService.obtenerEscrituraSocios(this.escritura.uuid).subscribe((data: EmpresaEscrituraSocio[]) => {
        this.escritura.socios = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar todos los socios. Motivo: ${error}`,
          ToastType.ERROR
        )
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El socio no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarApoderado(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Alguno de los parametros requeridos no se ha rellenado aun`,
        ToastType.WARNING
      );
      return;
    }

    if(this.tempUuidApoderado === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del apoderado a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el apoderado",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraApoderado = form.value;
    formValue.fechaBaja = formatDate(new Date(), "yyyy-MM-dd", "en")

    let formData = new FormData();
    formData.append('apoderado', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.legalService.eliminarEscrituraApoderado(this.escritura.uuid, this.tempUuidApoderado, formData).subscribe((data: EmpresaEscrituraApoderado) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el apoderado con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.tempFile = undefined;

      this.legalService.obtenerEscriturasApoderados(this.escritura.uuid).subscribe((data: EmpresaEscrituraApoderado[]) => {
        this.escritura.apoderados = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar todos los apoderados. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El apoderado no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarEscritura() {
    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando la escritura",
      ToastType.INFO
    );

    this.legalService.eliminarEscritura(this.escritura.uuid).subscribe((data: EmpresaEscritura) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado la escritura con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido eliminar la escritura. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarRepresentante(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Alguno de los parametros requeridos no se ha rellenado aun`,
        ToastType.WARNING
      );
      return;
    }

    if(this.tempUuidRepresentante === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del representante a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el representante",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraRepresentante = form.value;
    formValue.fechaBaja = formatDate(new Date(), "yyyy-MM-dd", "en")

    let formData = new FormData();
    formData.append('representante', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.legalService.eliminarEscrituraRepresentante(this.escritura.uuid, this.tempUuidRepresentante, formData).subscribe((data: EmpresaEscrituraRepresentante) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el representante con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.tempFile = undefined;

      this.legalService.obtenerEscrituraRepresentantes(this.escritura.uuid).subscribe((data: EmpresaEscrituraRepresentante[]) => {
        this.escritura.representantes = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar todos los representantes. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El representante no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarConsejo(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `Alguno de los parametros requeridos no se ha rellenado aun`,
        ToastType.WARNING
      );
      return;
    }

    if(this.tempUuidConsejo === undefined) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El UUID del miembro del consejo a eliminar no esta definido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Se esta eliminando el miembro del consejo",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraConsejo = form.value;
    formValue.fechaBaja = formatDate(new Date(), "yyyy-MM-dd", "en")

    let formData = new FormData();
    formData.append('consejo', JSON.stringify(formValue));

    if(this.tempFile !== undefined) {
      formData.append('archivo', this.tempFile, this.tempFile.name);
    } else {
      formData.append('archivo', null)
    }

    this.legalService.eliminarEscrituraConsejo(this.escritura.uuid, this.tempUuidConsejo, formData).subscribe((data: EmpresaEscrituraConsejo) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el miembro consejo con exito",
        ToastType.SUCCESS
      );
      this.modal.close();
      this.tempFile = undefined;

      this.legalService.obtenerEscrituraConsejos(this.escritura.uuid).subscribe((data: EmpresaEscrituraConsejo[]) => {
        this.escritura.consejos = data;
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se han podido descargar todos los consejos. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El miembro del consejo no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  clear(table: Table) {
    table.clear();
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
