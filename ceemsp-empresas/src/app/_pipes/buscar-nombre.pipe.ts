import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'buscarNombrePipe',
  pure: false
})
export class BuscarNombrePipe implements PipeTransform {

  transform(items: any[], filter: any): unknown {
    if (!items || !filter) {
      return items;
    }
    return items.filter(item => item.nombre.toLowerCase().indexOf(filter.nombre.toLowerCase()) !== -1);
  }

}
