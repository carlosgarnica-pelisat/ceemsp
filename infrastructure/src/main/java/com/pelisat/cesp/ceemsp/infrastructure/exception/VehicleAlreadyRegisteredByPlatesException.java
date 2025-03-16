package com.pelisat.cesp.ceemsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "El vehiculo con estas placas ya se encuentra registrado")
public class VehicleAlreadyRegisteredByPlatesException extends RuntimeException {
}
