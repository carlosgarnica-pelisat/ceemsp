import {Component, OnInit} from '@angular/core';
import Empresa from "../../../_models/Empresa";
import {ToastService} from "../../../_services/toast.service";
import {EmpresaService} from "../../../_services/empresa.service";
import {ActivatedRoute} from "@angular/router";
import {ToastType} from "../../../_enums/ToastType";

@Component({
  selector: 'app-empresa-detalles',
  templateUrl: './empresa-detalles.component.html',
  styleUrls: ['./empresa-detalles.component.css']
})
export class EmpresaDetallesComponent implements OnInit {

  empresa: Empresa;
  uuid: string;

  constructor(private toastService: ToastService, private empresaService: EmpresaService,
              private route: ActivatedRoute) { }

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
    })
  }

}
