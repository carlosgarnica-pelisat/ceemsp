import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'buscarPlacas'
})
export class BuscarPlacasPipe implements PipeTransform {

  transform(items: any[], filter: any): unknown {
    if (!items || !filter) {
      return items;
    }
    return items.filter(item => item.placas.toLowerCase().indexOf(filter.placas.toLowerCase()) !== -1);
  }

}
