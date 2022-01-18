import {Component, OnInit} from '@angular/core';
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
import {faCheck} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-empresa-vehiculos',
  templateUrl: './empresa-vehiculos.component.html',
  styleUrls: ['./empresa-vehiculos.component.css']
})
export class EmpresaVehiculosComponent implements OnInit {

  private gridApi;
  private gridColumnApi;

  columnDefs = Vehiculo.obtenerColumnasPorDefault();
  allColumnDefs = Vehiculo.obtenerTodasLasColumnas();

  rowData = [];
  vehiculo: Vehiculo;

  showColorForm: boolean = false;

  pestanaActual: string = "DETALLES";

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  closeResult: string;
  rowDataClicked = {
    uuid: undefined
  };

  crearVehiculoMarcaForm: FormGroup;
  crearVehiculoSubmarcaForm: FormGroup;

  crearVehiculoForm: FormGroup;
  crearColorForm: FormGroup;

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

  constructor(private modalService: NgbModal, private toastService: ToastService,
              private empresaService: EmpresaService, private formBuilder: FormBuilder,
              private vehiculosService: VehiculosService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.crearVehiculoForm = this.formBuilder.group({
      placas: ['', Validators.required],
      serie: ['', Validators.required],
      tipo: ['', Validators.required],
      marca: ['', Validators.required],
      submarca: ['', Validators.required],
      anio: ['', Validators.required],
      rotulado: ['', Validators.required],
      uso: ['', Validators.required],
      domicilio: ['', Validators.required],
      origen: ['', Validators.required],
      blindado: ['', Validators.required],
      serieBlindaje: [''],
      fechaBlindaje: [''],
      numeroHolograma: [''],
      placaMetalica: [''],
      empresaBlindaje: [''],
      nivelBlindaje: [''],
      razonSocial: [''],
      fechaInicio: [''],
      fechaFin: ['']
      // TODO: Agregar campos para fotos y documentos; asi como constancia de blindaje
    })

    this.crearColorForm = this.formBuilder.group({
      color: ['', Validators.required],
      descripcion: ['', Validators.required]
    })

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
    /*if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Faltan algunos campos obligatorios por llenarse",
        ToastType.WARNING
      );
      return;
    }*/
    switch (stepName) {
      case "INFORMACION":
        let formValue: Vehiculo = form.value;

        formValue.marca = this.marcas.filter(x => x.uuid === form.value.marca)[0];
        formValue.submarca = this.submarcas.filter(x => x.uuid === form.value.submarca)[0];
        formValue.tipo = this.tipos.filter(x => x.uuid === form.value.tipo)[0];

        if(this.blindado) {

        } else {
          formValue.nivelBlindaje = null
        }

        this.empresaService.guardarVehiculo(this.uuid, formValue).subscribe((data: Vehiculo) => {
          this.toastService.showGenericToast(
            "Listo",
            "Se ha guardado el vehiculo con exito",
            ToastType.SUCCESS
          );
          this.stepper.next();
        }, (error) => {
          this.toastService.showGenericToast(
            "Ocurrio un problema",
            `No se ha podido guardar el vehiculo. ${error}`,
            ToastType.ERROR
          )
        });
        break;
      /*case "DOMICILIOS":

        break;*/
    }
  }

  previous() {
    //this.stepper.previous()
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
