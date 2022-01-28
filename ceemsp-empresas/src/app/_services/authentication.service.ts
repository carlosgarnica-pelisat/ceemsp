import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {BehaviorSubject, Observable} from "rxjs";
import Usuario from "../_models/Usuario";
import {HttpClient} from "@angular/common/http";
import Credential from "../_models/Credential";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private uri = environment.apiUrl + environment.apiVersion;

  private currentUserSubject: BehaviorSubject<Usuario>;
  public currentUser: Observable<Usuario>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<Usuario>((JSON.parse(localStorage.getItem('currentUser'))));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): Usuario {
    return this.currentUserSubject.value;
  }

  login(credential: Credential) {
    return this.http.post<any>(`${this.uri}/login`, {email: credential.email, password: credential.password})
      .pipe(map(usuario => {
        localStorage.setItem('currentUser', JSON.stringify(usuario));
        this.currentUserSubject.next(usuario);
        return usuario;
      }))
  }

  logout() {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  doLogout() {
    return this.http.post<any>(`${this.uri}/logout`, {})
      .pipe(map(usuario => {
        localStorage.removeItem('currentUser');
        this.currentUserSubject.next(usuario);
        return usuario;
      }))
  }
}
