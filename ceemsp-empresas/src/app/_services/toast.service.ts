import {Injectable, TemplateRef} from '@angular/core';
import {ToastType} from "../_enums/ToastType";

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  toasts: any[] = [];

  constructor() { }

  // Push new toasts
  show(textOrTpl: string | TemplateRef<any>, options: any = {}) {
    this.toasts.push({ textOrTpl, ...options });
  }

  remove(toast) {
    this.toasts = this.toasts.filter(t => t !== toast);
  }

  showGenericToast(title: string, content: string, toastType: ToastType) {
    let classType: string = "";

    switch(toastType) {
      case ToastType.ERROR:
        classType = 'bg-danger text-light';
        break;
      case ToastType.INFO:
        classType = 'bg-info text-light';
        break;
      case ToastType.SUCCESS:
        classType = 'bg-success text-light'
        break;
      case ToastType.WARNING:
        classType = 'bg-warning text-light';
        break;
      default:
        console.warn("No toast set for enum given.");
        return;
    }

    this.show(
      content, {
        classname: classType,
        delay: 5000 ,
        autohide: true,
        headertext: title
      }
    );
  }
}
