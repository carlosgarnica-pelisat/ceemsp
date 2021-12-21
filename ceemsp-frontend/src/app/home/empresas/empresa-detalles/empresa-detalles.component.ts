import {Component, OnInit} from '@angular/core';
import Empresa from "../../../_models/Empresa";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import {ActivatedRoute} from "@angular/router";
import {ToastType} from "../../../_enums/ToastType";
import {ModalDismissReasons, NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import EmpresaModalidad from "../../../_models/EmpresaModalidad";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import Modalidad from "../../../_models/Modalidad";
import {ModalidadesService} from "../../../_services/modalidades.service";
import {faTrash, faPencilAlt} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-empresa-detalles',
  templateUrl: './empresa-detalles.component.html',
  styleUrls: ['./empresa-detalles.component.css']
})
export class EmpresaDetallesComponent implements OnInit {

  faPencil = faPencilAlt;
  faTrash = faTrash;

  empresa: Empresa;
  uuid: string;

  modal: NgbModalRef;
  closeResult: string;

  empresaModalidadForm: FormGroup;

  empresaModalidades: EmpresaModalidad[];
  modalidades: Modalidad[];
  modalidad: Modalidad;

  formularioModalidad: boolean = false;

  constructor(private toastService: ToastService, private empresaService: EmpresaService,
              private route: ActivatedRoute, private modalService: NgbModal,
              private formBuilder: FormBuilder, private modalidadService: ModalidadesService) { }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get("uuid");

    this.empresaService.obtenerPorUuid(this.uuid).subscribe((data: Empresa) => {
      this.empresa = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido descargar la informacion de la emprsa. ${error}`,
        ToastType.ERROR
      )
    });

    this.empresaModalidadForm = this.formBuilder.group({
      modalidad: ['', Validators.required],
      submodalidad: [''],
      numeroRegistroFederal: [''],
      fechaInicio: [''],
      fechaFin: ['']
    })
  }

  mostrarModalFormasEjecucion(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })
  }

  mostrarModalModalidades(modal) {
    this.modal = this.modalService.open(modal, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});

    this.modal.result.then((result) => {
      this.closeResult = `Closed with ${result}`;
    }, (error) => {
      this.closeResult = `Dismissed ${this.getDismissReason(error)}`
    })

    this.empresaService.obtenerModalidades(this.uuid).subscribe((data: EmpresaModalidad[]) => {
      this.empresaModalidades = data;
      console.log(data);
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo descargar la informacion de las modalidades de la empresa. ${error}`,
        ToastType.ERROR
      )
    })

    this.modalidadService.obtenerModalidadesPorFiltro("TIPO", this.empresa.tipoTramite).subscribe((data: Modalidad[]) => {
      this.modalidades = data;
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudieron descargar las modalidades. ${error}`,
        ToastType.ERROR
      )
    })
  }

  seleccionarModalidad(event) {
    this.modalidad = this.modalidades.filter(m => m.uuid === event.value)[0];
    if(this.modalidad.submodalidades.length > 0) {
      this.modalidad.tieneSubmodalidades = true;
    }
  }

  mostrarFormularioModalidad() {
    this.formularioModalidad = true;
  }

  ocultarFormularioModalidad() {
    this.formularioModalidad = false;
  }

  agregarModalidad(empresaModalidadForm) {
    if(!empresaModalidadForm.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido rellenados",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando la modalidad en la empresa",
      ToastType.INFO
    );

    let formData: EmpresaModalidad = empresaModalidadForm.value;

    this.empresaService.guardarModalidad(this.uuid, formData).subscribe((data: EmpresaModalidad) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado la modalidad en la empresa con exito",
        ToastType.SUCCESS
      );
      window.location.reload();
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se ha podido guardar la modalidad en la empresa. Motivo: ${error}`,
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
