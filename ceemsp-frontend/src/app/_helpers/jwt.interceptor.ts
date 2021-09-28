import { Injectable } from "@angular/core";
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from "@angular/common/http";
import { Observable } from "rxjs";
import { AuthenticationService } from "../_services/authentication.service";

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authenticationService: AuthenticationService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let currentUser = this.authenticationService.currentUserValue;
    if(currentUser && currentUser.jwtToken) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer: ${currentUser.jwtToken}`,
          'Content-Type': `application/json`
        }
      })
    }

    return next.handle(req);
  }
}
