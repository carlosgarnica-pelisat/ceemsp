import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ComunicadosGeneralesService} from "../../../../_services/comunicados-generales.service";
import * as ClassicEditor from '@ckeditor/ckeditor5-editor-classic';
import {ToastService} from "../../../../_services/toast.service";
import {ToastType} from "../../../../_enums/ToastType";
import {SanitizeHtmlPipe} from "../../../../_pipes/sanitize-html.pipe";
import ComunicadoGeneral from "../../../../_models/ComunicadoGeneral";
import {Router} from "@angular/router";


@Component({
  selector: 'app-comunicado-general-nuevo',
  templateUrl: './comunicado-general-nuevo.component.html',
  styleUrls: ['./comunicado-general-nuevo.component.css']
})
export class ComunicadoGeneralNuevoComponent implements OnInit {

  public editor = ClassicEditor;
  nuevoComunicadoForm: FormGroup;
  model = {
    editorData: '<p>Escribe con detalle el comunicado. Puedes utilizar los botones de la parte superior</p>'
  }
  rowData: ComunicadoGeneral[] = [];
  fechaDeHoy = new Date().toISOString().split('T')[0];

  constructor(private formBuilder: FormBuilder, private comunicadosGeneralesService: ComunicadosGeneralesService,
              private toastService: ToastService, private sanitizeHtmlPipe: SanitizeHtmlPipe, private router: Router,) { }

  ngOnInit(): void {
    this.nuevoComunicadoForm = this.formBuilder.group({
      titulo: ['', [Validators.required, Validators.maxLength(100)]],
      fechaPublicacion: ['', Validators.required]
    })
  }

  guardarComunicado(form) {
    if(!form.valid) {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "Hay campos requeridos que no han sido guardados",
        ToastType.WARNING
      );
      return;
    }

    if(this.model.editorData === "") {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        "El campo del comunicado no tiene contenido",
        ToastType.WARNING
      );
      return;
    }

    this.toastService.showGenericToast(
      "Espere un momento",
      "Estamos guardando el comunicado",
      ToastType.INFO
    );

    let formData: ComunicadoGeneral = form.value;
    formData.descripcion = this.sanitizeHtmlPipe.transform(this.model.editorData)['changingThisBreaksApplicationSecurity'];

    this.comunicadosGeneralesService.guardarComunicado(formData).subscribe((data: ComunicadoGeneral) => {
      this.toastService.showGenericToast(
        "Listo",
        "Se ha guardado el comunicado con exito",
        ToastType.SUCCESS
      );
      this.router.navigate(['/home/comunicados/generales']);
    }, (error) => {
      this.toastService.showGenericToast(
        "Ocurrio un problema",
        `No se pudo guardar el comunicado. Motivo: ${error}`,
        ToastType.ERROR
      );
    })
  }

}
