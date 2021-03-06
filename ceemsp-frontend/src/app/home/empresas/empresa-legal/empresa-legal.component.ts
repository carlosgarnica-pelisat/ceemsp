import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import EmpresaEscritura from "../../../_models/EmpresaEscritura";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import {ToastType} from "../../../_enums/ToastType";
import EmpresaEscrituraSocio from "../../../_models/EmpresaEscrituraSocio";
import {faCheck, faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import EmpresaEscrituraApoderado from "../../../_models/EmpresaEscrituraApoderado";
import EmpresaEscrituraRepresentante from "../../../_models/EmpresaEscrituraRepresentante";
import EmpresaEscrituraConsejo from "../../../_models/EmpresaEscrituraConsejo";
import {EstadosService} from "../../../_services/estados.service";
import Estado from "../../../_models/Estado";
import Municipio from "../../../_models/Municipio";
import curp from 'curp';

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

  modal: NgbModalRef;
  closeResult: string;
  escritura: EmpresaEscritura;

  faPencil = faPencilAlt;
  faTrash = faTrash;
  faCheck = faCheck;

  private gridApi;
  private gridColumnApi;

  columnDefs = EmpresaEscritura.obtenerColumnasPorDefault();
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

  estado: Estado;
  municipio: Municipio;

  porcentaje: number = 0.00;

  tempUuidSocio: string;
  tempUuidApoderado: string;
  tempUuidRepresentante: string;
  tempUuidConsejo: string;

  @ViewChild('mostrarDetallesEscrituraModal') mostrarDetallesEscrituraModal: any;
  @ViewChild('modificarEscrituraModal') modificarEscrituraModal: any;

  @ViewChild('eliminarEscrituraSocioModal') eliminarEscrituraSocioModal;
  @ViewChild('eliminarEscrituraApoderadoModal') eliminarEscrituraApoderadoModal;
  @ViewChild('eliminarEscrituraRepresentanteModal') eliminarEscrituraRepresentanteModal;
  @ViewChild('eliminarEscrituraConsejoModal') eliminarEscrituraConsejoModal;
  @ViewChild('eliminarEscrituraModal') eliminarEscrituraModal;

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaService: EmpresaService,
              private formBuilder: FormBuilder, private estadoService: EstadosService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.nuevaEscrituraForm = this.formBuilder.group({
      numeroEscritura: ['', [Validators.required, Validators.maxLength(10)]],
      fechaEscritura: ['', Validators.required],
      ciudad: ['', [Validators.required, Validators.maxLength(60)]],
      tipoFedatario: ['', Validators.required],
      numero: ['', [Validators.required, Validators.min(1), Validators.max(9999)]],
      nombreFedatario: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', Validators.required]
    })

    this.nuevoSocioForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      porcentajeAcciones: ['', Validators.required],
      curp: ['', [Validators.required, Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.nuevoApoderadoForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required],
      curp: ['', [Validators.required, Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.nuevoRepresentanteForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      curp: ['', [Validators.required, Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.nuevoConsejoAdministracionForm = this.formBuilder.group({
      nombres: ['', [Validators.required, Validators.maxLength(60)]],
      apellidos: ['', [Validators.required, Validators.maxLength(60)]],
      apellidoMaterno: ['', [Validators.maxLength(60)]],
      sexo: ['', Validators.required],
      puesto: ['', Validators.required],
      curp: ['', [Validators.required, Validators.minLength(18), Validators.maxLength(18)]]
    })

    this.empresaService.obtenerEscrituras(this.uuid).subscribe((data: EmpresaEscritura[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las escrituras. ${error}`,
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

  mostrarEditarSocio(index) {
    this.socio = this.escritura.socios[index];
    this.mostrarFormularioNuevoSocio();
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

  mostrarEditarApoderado(index) {
    this.apoderado = this.escritura.apoderados[index];
    this.mostrarFormularioNuevoApoderado();
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

  mostrarEditarRepresentante(index) {
    this.representante = this.escritura.representantes[index];
    this.mostrarFormularioNuevoRepresentante();
    this.editandoRepresentante = true;
    this.nuevoRepresentanteForm.patchValue({
      nombres: this.representante.nombres,
      apellidos: this.representante.apellidos,
      apellidoMaterno: this.representante.apellidoMaterno,
      curp: this.representante.curp,
      sexo: this.representante.sexo
    });
  }

  mostrarEditarConsejo(index) {
    this.consejo = this.escritura.consejos[index];
    this.mostrarFormularioNuevoConsejo();
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

  mostrarModalEliminarEscritura() {
    this.modal = this.modalService.open(this.eliminarEscrituraModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }


  mostrarModalEliminarSocio(uuid) {
    this.tempUuidSocio = uuid;
    this.modal = this.modalService.open(this.eliminarEscrituraSocioModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarApoderado(uuid) {
    this.tempUuidApoderado = uuid;
    this.modal = this.modalService.open(this.eliminarEscrituraApoderadoModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarRepresentante(uuid) {
    this.tempUuidRepresentante = uuid;
    this.modal = this.modalService.open(this.eliminarEscrituraRepresentanteModal, {ariaLabelledBy: 'modal-basic-title', size: 'lg'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalEliminarConsejo(uuid) {
    this.tempUuidConsejo = uuid;
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

    if(this.escritura.socios.length > 0) {
      this.porcentaje = 0.00;
      this.escritura.socios.forEach((s) => {
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

      this.empresaService.modificarEscrituraSocio(this.uuid, this.escritura.uuid, this.socio.uuid, formValue).subscribe((data: EmpresaEscrituraSocio) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el socio con exito",
          ToastType.SUCCESS
        );
        window.location.reload();
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el socio. ${error}`,
          ToastType.ERROR
        );
      });

    } else {
      this.empresaService.guardarEscrituraSocio(this.uuid, this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraSocio) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha registrado el socio con exito",
          ToastType.SUCCESS
        );
        window.location.reload();
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

      this.empresaService.modificarEscrituraApoderado(this.uuid, this.escritura.uuid, this.apoderado.uuid, formValue).subscribe((data: EmpresaEscrituraApoderado) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el apoderado con exito",
          ToastType.SUCCESS
        );
        window.location.reload();
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el apoderado. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
    } else {
      this.empresaService.guardarEscrituraApoderado(this.uuid, this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraApoderado) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha registrado el apoderado con exito",
          ToastType.SUCCESS
        );
        window.location.reload();
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

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el nuevo consejo",
      ToastType.INFO
    );

    let formValue: EmpresaEscrituraConsejo = nuevoConsejoForm.value;

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

    let existeConsejoPuesto = this.escritura.consejos.filter(x => x.puesto === formValue.puesto)
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

      this.empresaService.modificarEscrituraConsejo(this.uuid, this.escritura.uuid, this.consejo.uuid, formValue).subscribe((data: EmpresaEscrituraConsejo) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha modificado el miembro del consejo con exito",
          ToastType.SUCCESS
        );
        window.location.reload();
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido modificar el miembro del consejo. Motivo: ${error}`,
          ToastType.ERROR
        );
      });
    } else {
      this.empresaService.guardarEscrituraConsejos(this.uuid, this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraConsejo) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha registrado el miembro del consejo con exito",
          ToastType.SUCCESS
        );
        window.location.reload();
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

    if(this.representante) {
      formValue.uuid = this.representante.uuid;
      formValue.id = this.representante.id;

      this.empresaService.modificarEscrituraRepresentante(this.uuid, this.escritura.uuid, this.representante.uuid, formValue).subscribe((data: EmpresaEscrituraRepresentante) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha actualizado el representante con exito",
          ToastType.SUCCESS
        );
        window.location.reload();
      }, (error) => {
        this.toastService.showGenericToast(
          "Ocurrio un problema",
          `No se ha podido actualizar el representante. Motivo: ${error}`,
          ToastType.ERROR
        );
      })
    } else {
      this.empresaService.guardarEscrituraRepresentante(this.uuid, this.escritura.uuid, formValue).subscribe((data: EmpresaEscrituraRepresentante) => {
        this.toastService.showGenericToast(
          "Listo",
          "Se ha registrado el representante con exito",
          ToastType.SUCCESS
        );
        window.location.reload();
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

  mostrarModalCrear(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
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

  modify(rowData) {

  }

  delete(rowData) {

  }

  mostrarModalDetalles(rowData, modal) {
    let escrituraUuid = rowData.uuid;
    this.empresaService.obtenerEscrituraPorUuid(this.uuid, escrituraUuid).subscribe((data: EmpresaEscritura) => {
      this.escritura = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la escritura. ${error}`,
        ToastType.ERROR
      )
    })

    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl', scrollable: true});

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

  guardarEscritura(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no estan siendo llenados",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la escritura",
      ToastType.INFO
    );

    let formValue: EmpresaEscritura = form.value;

    let formData = new FormData();
    formData.append('archivo', this.tempFile, this.tempFile.name);
    formData.append('escritura', JSON.stringify(formValue));

    this.empresaService.guardarEscritura(this.uuid, formData).subscribe((data: EmpresaEscritura) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la escritura con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la escritura. ${error}`,
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

    this.empresaService.descargarEscrituraPdf(this.uuid, this.escritura.uuid).subscribe((data: Blob) => {
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

  mostrarModificarEscrituraModal() {
    this.nuevaEscrituraForm.setValue({
      numeroEscritura: this.escritura.numeroEscritura,
      fechaEscritura: this.escritura.fechaEscritura,
      ciudad: this.escritura.ciudad,
      tipoFedatario: this.escritura.tipoFedatario,
      numero: this.escritura.numero,
      nombreFedatario: this.escritura.nombreFedatario,
      descripcion: this.escritura.descripcion
    });

    this.modalService.dismissAll();

    this.modalService.open(this.modificarEscrituraModal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`;
    })
  }

  guardarCambiosEscritura(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario es invalido. Favor de verificar los campos",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando los cambios en la escritura",
      ToastType.INFO
    );

    let escritura: EmpresaEscritura = form.value;

    this.empresaService.modificarEscritura(this.uuid, this.escritura.uuid, escritura).subscribe((data: EmpresaEscritura) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se han guardado los cambios de la escritura con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido modificar la escritura. Motivo: ${error}`,
        ToastType.ERROR
      )
    });
  }

  confirmarEliminarSocio() {
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

    this.empresaService.eliminarEscrituraSocio(this.uuid, this.escritura.uuid, this.tempUuidSocio).subscribe((data: EmpresaEscrituraSocio) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el socio con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El socio no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarApoderado() {
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

    this.empresaService.eliminarEscrituraApoderado(this.uuid, this.escritura.uuid, this.tempUuidApoderado).subscribe((data: EmpresaEscrituraApoderado) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el apoderado con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
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

    this.empresaService.eliminarEscritura(this.uuid, this.escritura.uuid).subscribe((data: EmpresaEscritura) => {
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

  confirmarEliminarRepresentante() {
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

    this.empresaService.eliminarEscrituraRepresentante(this.uuid, this.escritura.uuid, this.tempUuidRepresentante).subscribe((data: EmpresaEscrituraRepresentante) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el representante con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El representante no se ha podido eliminar. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  confirmarEliminarConsejo() {
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

    this.empresaService.eliminarEscrituraConsejo(this.uuid, this.escritura.uuid, this.tempUuidConsejo).subscribe((data: EmpresaEscrituraConsejo) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha eliminado el miembro consejo con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `El miembro del consejo no se ha podido eliminar. Motivo: ${error}`,
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
