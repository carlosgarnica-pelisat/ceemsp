import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'buscarRazonSocial',
  pure: false
})
export class BuscarRazonSocialPipe implements PipeTransform {

  transform(items: any[], filter: any): unknown {
    if (!items || !filter) {
      return items;
    }
    return items.filter(item => item.razonSocial.toLowerCase().indexOf(filter.razonSocial.toLowerCase()) !== -1);
  }

}
