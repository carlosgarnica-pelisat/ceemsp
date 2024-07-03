import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'buscarMatricula'
})
export class BuscarMatriculaPipe implements PipeTransform {

  transform(items: any[], filter: any): unknown {
    if (!items || !filter) {
      return items;
    }
    return items.filter(item => item.matricula.toLowerCase().indexOf(filter.matricula.toLowerCase()) !== -1);
  }

}
