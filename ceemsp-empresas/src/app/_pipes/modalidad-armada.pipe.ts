import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'modalidadArmada'
})
export class ModalidadArmadaPipe implements PipeTransform {

  transform(items: any[], filter: any): unknown {
    if (!items || !filter) {
      return items;
    }
    return items.filter(item => item?.modalidad?.armas === filter.armas);
  }

}
