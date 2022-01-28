import {Component, OnInit} from '@angular/core';
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

@Component({
  selector: 'app-empresa-clientes',
  templateUrl: './empresa-clientes.component.html',
  styleUrls: ['./empresa-clientes.component.css']
})
export class EmpresaClientesComponent implements OnInit {

  uuid: string;

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
  nuevoClienteDomicilioForm: FormGroup;

  domicilios: ClienteDomicilio[] = [];

  tiposInfraestructura: TipoInfraestructura[] = [];
  tipoInfraestructura: TipoInfraestructura = undefined;

  pestanaActual: string = "DETALLES";

  constructor(private route: ActivatedRoute, private toastService: ToastService,
              private modalService: NgbModal, private empresaService: EmpresaService,
              private formBuilder: FormBuilder, private tipoInfraestructuraService: TipoInfraestructuraService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.nuevoClienteForm = this.formBuilder.group({
      tipoPersona: ['', Validators.required],
      rfc: ['', Validators.required],
      nombreComercial: ['', Validators.required],
      razonSocial: ['', Validators.required],
      canes: ['', Validators.required],
      armas: ['', Validators.required],
      fechaInicio: ['', Validators.required]
    });

    this.nuevoClienteDomicilioForm = this.formBuilder.group({
      nombre: ['', Validators.required],
      domicilio1: ['', Validators.required],
      domicilio2: ['', Validators.required],
      domicilio3: ['', Validators.required],
      domicilio4: [''],
      codigoPostal: ['', Validators.required],
      estado: ['', Validators.required],
      pais: ['Mexico', Validators.required],
      matriz: ['', Validators.required], // TODO: Quitar el si/no y agregar tipo de domicilio como matriz / sucursal
      contacto: ['', Validators.required],
      telefonoFijo: ['', Validators.required],
      telefonoMovil: ['', Validators.required],
      correoElectronico: ['', Validators.required],
      tipoInfraestructura: ['', Validators.required],
      tipoInfraestructuraOtro: ['']
    })

    this.empresaService.obtenerClientes(this.uuid).subscribe((data: Cliente[]) => {
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
    })
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

  modify(rowData) {

  }

  delete(rowData) {

  }

  seleccionarTipoInfraestructura(target) {
    let uuid = target.value;
    this.tipoInfraestructura = this.tiposInfraestructura.filter(x => x.uuid === uuid)[0];
  }

  next(stepName: string, form) {
    /*if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenarse",
        ToastType.WARNING
      );
      return;
    }*/
    this.stepper.next();

    switch (stepName) {
      /*case "INFORMACION":
        let formData: Cliente = form.value;
        let formValue: Cliente = form.value;

        this.empresaService.guardarCliente(this.uuid, formValue).subscribe((data: Cliente) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se ha guardado el cliente con exito",
            ToastType.SUCCESS
          );
          this.stepper.next();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido guardar el cliente. ${error}`,
            ToastType.ERROR
          )
        });
        break;
      case "DOMICILIOS":

        break;*/
    }


    this.stepper.next();
  }

  previous() {
    this.stepper.previous();
  }

  mostrarModalDetalles(rowData, modal) {

    let clienteUuid = rowData.uuid;

    this.empresaService.obtenerClientePorUuid(this.uuid, clienteUuid).subscribe((data: Cliente) => {
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
    let tempArray: ClienteDomicilio[] = [domicilio];

    this.empresaService.guardarDomicilioCliente(this.uuid, this.cliente.uuid, tempArray).subscribe((data: ClienteDomicilio) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el domicilio del cliente con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar el domicilio. Motivo: ${error}`,
        ToastType.ERROR
      )
    });
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
    this.domicilios.push(formData);
    this.nuevoClienteDomicilioForm.reset();
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
