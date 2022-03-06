import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class CalleService {
  private uri: String = environment.apiUrl + environment.apiVersion;

  constructor(private http: HttpClient) { }

  obtenerCallesPorQuery(query: string) {
    return this.http.get(`${this.uri}/catalogos/calles?query=${query}`)
  }

  obtenerCallesPorLimite(limite: number) {
    return this.http.get(`${this.uri}/catalogos/calles?limit=${limite}`);
  }
}
