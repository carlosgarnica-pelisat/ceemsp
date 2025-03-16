import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'buscarNombres'
})
export class BuscarNombresPipe implements PipeTransform {

  transform(items: any[], filter: any): unknown {
    if (!items || !filter) {
      return items;
    }
    return items.filter(item => item.nombres.toLowerCase().indexOf(filter.nombres.toLowerCase()) !== -1);
  }

}
