import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ToastService} from "../../_services/toast.service";
import {EmpresaService} from "../../_services/empresa.service";
import Empresa from "../../_models/Empresa";
import {ToastType} from "../../_enums/ToastType";
import {Router} from "@angular/router";
import {faCheck} from "@fortawesome/free-solid-svg-icons";
import {BotonEmpresasComponent} from "../../_components/botones/boton-empresas/boton-empresas.component";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-empresas',
  templateUrl: './empresas.component.html',
  styleUrls: ['./empresas.component.css']
})
export class EmpresasComponent implements OnInit {

  faCheck = faCheck;

  private gridApi;
  private gridColumnApi;

  columnDefs =
    [
      {headerName: 'Nombre comercial', field: 'nombreComercial', sortable: true, filter: true },
      {headerName: 'Registro', field: 'registro', sortable: true, filter: true },
      {headerName: 'Razon social', field: 'razonSocial', sortable: true, filter: true},
      {headerName: 'Tipo persona', field: 'tipoPersona', sortable: true, filter: true},
      {headerName: 'Acciones', cellRenderer: 'buttonRenderer', cellRendererParams: {
          label: 'Ver detalles',
          verDetalles: this.verDetalles.bind(this),
          editar: this.editar.bind(this),
          cambiarStatus: this.cambiarStatus.bind(this)
        }}
    ];
  allColumnDefs = Empresa.obtenerTodasLasColumnas();

  rowData = [];

  uuid: string;
  modal: NgbModalRef;
  frameworkComponents: any;
  rowDataClicked = {
    uuid: undefined
  };
  empresa: Empresa;
  empresaCreacionForm: FormGroup;
  empresaCambioStatusForm: FormGroup;
  tipoPersona: string;

  @ViewChild('editarEmpresaModal') editarEmpresaModal;
  @ViewChild('editarStatusModal') editarStatusModal;

  verDetalles(rowData) {
    this.checkForDetails(rowData.rowData)
  }

  constructor(private toastService: ToastService, private empresaService: EmpresaService, private router: Router,
              private formBuilder: FormBuilder, private modalService: NgbModal) { }

  ngOnInit(): void {
    this.frameworkComponents = {
      buttonRenderer: BotonEmpresasComponent
    }

    this.empresaService.obtenerEmpresas().subscribe((data: Empresa[]) => {
      this.rowData = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se han podido descargar las empresas. ${error}`,
        ToastType.ERROR
      )
    })

    this.empresaCreacionForm = this.formBuilder.group({
      tipoPersona: ['', Validators.required],
      razonSocial: ['', [Validators.required, Validators.maxLength(100)]],
      nombreComercial: ['', [Validators.required, Validators.maxLength(100)]],
      rfc: ['', [Validators.required, Validators.minLength(12), Validators.maxLength(13)]],
      curp: ['', [Validators.minLength(18), Validators.maxLength(18)]],
      sexo: [''],
      correoElectronico: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
      telefono: ['', [Validators.required]],
      observaciones: ['']
    });

    this.empresaCambioStatusForm = this.formBuilder.group({
      status: ['', Validators.required],
      observaciones: ['', Validators.required]
    });
  }

  cambiarStatusEmpresa(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no se han completado",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espera un momento",
      "Estamos guardando el nuevo status de la empresa",
      ToastType.INFO
    );

    let value: Empresa = form.value;

    this.empresaService.cambiarStatusEmpresa(this.uuid, value).subscribe((data: Empresa) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha cambiado el status de la empresa",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido cambiar el status de la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params.api;
    this.gridColumnApi = params.gridApi;
  }

  checkForDetails(data) {
    this.uuid = data.uuid;

    this.router.navigate([`/home/empresas/${this.uuid}`]);
  }

  editar(rowData) {
    this.empresaService.obtenerPorUuid(rowData.rowData?.uuid).subscribe((data: Empresa) => {
      this.empresa = data;

      this.empresaCreacionForm.patchValue({
        tipoPersona: this.empresa.tipoPersona,
        nombreComercial: this.empresa.nombreComercial,
        razonSocial: this.empresa.razonSocial,
        rfc: this.empresa.rfc,
        sexo: this.empresa.sexo,
        curp: this.empresa.curp,
        correoElectronico: this.empresa.correoElectronico,
        telefono: this.empresa.telefono,
        observaciones: this.empresa.observaciones
      })

      this.tipoPersona = this.empresa.tipoPersona;

      this.modalService.open(this.editarEmpresaModal,{size: "xl"})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

  cambiarTipoPersona(event) {
    this.tipoPersona = event.value;
  }

  cambiarStatus(rowData) {
    this.empresaService.obtenerPorUuid(rowData.rowData?.uuid).subscribe((data: Empresa) => {
      this.empresa = data;
      this.modalService.open(this.editarStatusModal,{size: "xl"})
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la empresa. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
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

      this.columnDefs.push(newColumnDef);
      this.gridApi.setColumnDefs(this.columnDefs);
    } else {
      this.columnDefs = this.columnDefs.filter(s => s.field !== field);
    }
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

  modificarEmpresa(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El formulario es invalido. Favor de verificarlo",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos modificando la empresa",
      ToastType.INFO
    );

    let formValue: Empresa = form.value;

    this.empresaService.modificarEmpresa(this.empresa.uuid, formValue).subscribe((data: Empresa) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha actualizado la empresa con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido actualizar la empresa. Motivo: ${error}`,
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
