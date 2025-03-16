package com.pelisat.cesp.ceemsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "El vehiculo con este numero de serie ya se encuentra registrado")
public class VehicleAlreadyRegisteredBySerialException extends RuntimeException {
}
